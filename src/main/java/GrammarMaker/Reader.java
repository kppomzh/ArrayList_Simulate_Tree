package GrammarMaker;

import Exceptions.GrammerMakerError.Impl.*;
import Lex.Lex;
import Parser.Parser;
import Utils.JavaPoet.makeBranchTreeNode;
import bean.GrammerMaker.RuleInfo;
import bean.GrammerMaker.LanguageNodeProperty;
import bean.KVEntryImpl;
import bean.Lex.IdentifierSetter;
import bean.Parser.PredictiveAnalysisTable;
import bean.Parser.Rule;

import java.io.*;
import java.util.*;

/**
 * Reader类是低配Antlr的总控分析程序，里面包含了词法部分和语法部分
 * 但是与Antlr不同的是，词法识别并没有那么智能。因为不支持正则表达式的分析。
 * Reader类会根据grammer文件生成对应的文法分析类
 */
public class Reader {
    private static final String[] FixLoadGrammer={"annotation.grammer","keyword.grammer","mark.grammer"};
    private int tokenNum=0;
    private Collection<String> grammerFileList;
    private GrammerFileReader grammerReader;
    /**
     * 记录文法符号的信息，包括产生式右部，first集，follow集
     */
    private Map<String, RuleInfo> ruleMap;
    /**
     * ruleNameSet记录非终结符存在性
     * keyNameSet记录终结符存在性
     * markCollection记录标点符号，未来分析AST类的时候要用
     */
    private Set<String> ruleNameSet,keyNameSet;
    private Collection<String> markCollection;

    public Reader(String basePath) throws IOException {
        ruleNameSet =new HashSet<>();
        keyNameSet=new HashSet<>();
        grammerReader=new GrammerFileReader(basePath);
        grammerFileList=grammerReader.getLinesinFile("grammer.list");
        ruleMap = new HashMap<>();
    }

    /**
     * 生成Lex
     * @return
     * @throws IOException
     * @throws TokenisRepeat
     */
    public Lex LexGenerate() throws IOException, TokenisRepeat {
        String[] anno = new String[0],key = new String[0],mark = new String[0];

        for (int i = 0; i < 3; i++) {
            Collection<String> collection=grammerReader.getLinesinFile(FixLoadGrammer[i]);
            switch (FixLoadGrammer[i]){
                case "annotation.grammer":
                    anno=collection.toArray(new String[0]);
                    break;
                case "keyword.grammer":
                    key=collection.toArray(new String[0]);
                    break;
                case "mark.grammer":
                    markCollection=collection;
                    mark=collection.toArray(new String[0]);
                    break;
            }
            tokenNum+=collection.size();
            keyNameSet.addAll(collection);
        }
        if(tokenNum!=keyNameSet.size()){
            throw new TokenisRepeat();
        }
        keyNameSet.add("ε");

        return new Lex(new IdentifierSetter(anno,mark,key));
    }

    /**
     * 生成Parser
     * @return
     * @throws GrammerUndefined
     * @throws IOException
     */
    public Parser ParserGenerate() throws GrammerUndefined, IOException, LeftCommonFactorConflict, FollowDebugException {
        for(String filename:grammerFileList){
            //遍历grammer.list中记录的文件
            //传递到makeRule中
            makeRule(grammerReader.makeConbinationGrammer(grammerReader.getLinesinFile(filename+".grammer")));
        }

        countFirstCollection("S",new HashSet<>());
        countFollowCollection();

        return new Parser(makeMap());
    }

    /**
     * 生成位于Tree_Span.Impl包下的AST数据结构
     * @return
     */
    public List<File> ASTGenerate() throws ClassNotFoundException, IOException, InfinityRightRecursion {
        List<File> javaFiles=new LinkedList<>();
        ASTPropertiesMaker maker=new ASTPropertiesMaker(ruleNameSet,ruleMap,markCollection);
        Collection<LanguageNodeProperty> properties=maker.countASTProperties();
        for (LanguageNodeProperty prop:properties){
            //根据统计信息生成AST类
            makeBranchTreeNode node=new makeBranchTreeNode(prop,this.ruleNameSet);
            node.AnalysisChildPropClass();
            javaFiles.add(node.buildFile());
        }
        return javaFiles;
    }

    /**
     * 根据非终结符信息构造分析表
     * 当遇到终结符是"ε"时，应该遍历当前文法符号的follow集获取terminal，并将nonTerminal → ε这条产生式填入
     * ptable.setDriverTable(terminal,nonterminal,rule<nonTerminal → ε>)
     * @return
     */
    private PredictiveAnalysisTable makeMap() throws LeftCommonFactorConflict {
        PredictiveAnalysisTable ptable=new PredictiveAnalysisTable(keyNameSet,ruleNameSet);
        for(String rulename: ruleNameSet){
            String nonterminal=rulename;
            RuleInfo ri=ruleMap.get(nonterminal);

            for(Rule rule:ri.getRules()){
                Set<String> tempset;
                if(ruleNameSet.contains(rule.getFirstMark())){
                    tempset=ruleMap.get(rule.getFirstMark()).getFirstSet();
                }
                else if(rule==Rule.epsilon){
                    tempset=ri.getFollowSet();
                }
                else{
                    ptable.setDriverTable(rule.getFirstMark(), nonterminal, rule);
                    break;
                }

                for (String terminal : tempset) {
                    ptable.setDriverTable(terminal, nonterminal, rule);
                }
            }
        }

        return ptable;
    }

    /**
     * 统计文法符号之间的上下级关系 RuleTreeMap
     * 统计每个非终结符对应的产生式
     * @param prop
     * @throws GrammerUndefined
     */
    private void makeRule(List<KVEntryImpl<String,String>> prop) throws GrammerUndefined {
        for(KVEntryImpl<String,String> e:prop){
            ruleNameSet.add(e.getKey());
        }

        for(KVEntryImpl<String,String> e:prop){
            //rulemap中未记录则新建RuleInfo类
            if(!ruleMap.containsKey(e.getKey())){
                ruleMap.put(e.getKey(), new RuleInfo());
            }


            if(e.getValue().equals("ε")){
                ruleMap.get(e.getKey()).addRule(Rule.epsilon);
            }
            else {
                Rule r = new Rule();
                String[] rules = e.getValue().split(" ");

                for (String singalrule : rules) {
                    if (!(ruleNameSet.contains(singalrule) || keyNameSet.contains(singalrule))) {
                        throw new GrammerUndefined(singalrule);
                    }
                }

                r.setRules(new ArrayList<>(List.of(rules)));
                ruleMap.get(e.getKey()).addRule(r);
            }
        }
    }

    /**
     * 计算first集
     */
    private void countFirstCollection(String s,Set<String> stopValue){
        if(stopValue.contains(s)){
            return;
        }

        for(String child:this.ruleNameSet){
            RuleInfo ri=ruleMap.get(child);
            while(ri.hasNext()){
                String first=ri.getNextRule().getFirstMark();
                if(this.keyNameSet.contains(first)){
                    ri.addFirstSet(first);
                }
                else {
                    if(ruleMap.get(first).getFirstSet().size()==0){
                        stopValue.add(s);
                        countFirstCollection(first,stopValue);
                    }
                    ri.addFirstSet(ruleMap.get(first).getFirstSet());
                }
            }
        }
    }

    /**
     * 计算follow集
     */
    private void countFollowCollection() throws FollowDebugException {
        FollowCollectionMaker maker=new FollowCollectionMaker(ruleMap,ruleNameSet);
        ruleMap=maker.countFollowCollection();
    }
}
