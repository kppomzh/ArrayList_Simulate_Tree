package GrammarMaker;

import Exceptions.GrammerMakerError.GrammerBaseException;
import Exceptions.GrammerMakerError.Impl.*;
import Lex.Lex;
import Lex.LexRule;
import Parser.Parser;
import Utils.GrammerFileReader;
import Utils.JavaPoet.makeBranchTreeNode;
import Utils.env_properties;
import bean.GrammerMaker.LanguageNodeProperty;
import bean.GrammerMaker.nonTerminalMarkInfo;
import bean.KVEntryImpl;
import bean.Lex.IdentifierSetter;
import bean.Parser.PredictiveAnalysisTable;
import bean.Parser.Rule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
    private static GrammerFileReader grammerReader=new GrammerFileReader(
            env_properties.getEnvironment("grammerFilePath"));
    /**
     * 记录文法符号的信息，包括产生式右部，first集，follow集，select集
     */
    private Map<String, nonTerminalMarkInfo> ruleMap;
    /**
     * ruleNameSet记录非终结符存在性
     * keyNameSet记录终结符存在性
     * markCollection记录标点符号，未来分析AST类的时候要用
     */
    private Set<String> ruleNameSet,keyNameSet;
    private Collection<String> markCollection,KeyWordCollection;
    private LinkedList<String> toCountCollection;

    //get方法需要用到的全局变量
    private PredictiveAnalysisTable analysisTable;
    private String[] anno = new String[0],key = new String[0],mark = new String[0];
    private int[] charMap;

    public Reader() throws IOException {
        this(grammerReader.getLinesinFile("grammer.list"));
    }

    public Reader(Collection<String> grammerFile) throws IOException {
        ruleNameSet =new HashSet<>();
        keyNameSet=new HashSet<>();
        grammerFileList=grammerFile;
        ruleMap = new HashMap<>();
    }

    /**
     * 生成Lex
     * @return
     * @throws IOException
     * @throws TokenisRepeat
     */
    public void LexGenerate() throws IOException, GrammerBaseException {
        for (int i = 0; i < 3; i++) {
            Collection<String> collection=grammerReader.getLinesinFile(FixLoadGrammer[i]);
            switch (FixLoadGrammer[i]){
                case "annotation.grammer":
                    keyNameSet.addAll(collection);
                    anno=collection.toArray(new String[0]);
                    break;
                case "keyword.grammer":
                    KeyWordCollection=collection;
                    key=collection.toArray(new String[0]);
                    break;
                case "mark.grammer":
                    markCollection=collection;
                    mark=collection.toArray(new String[0]);
                    break;
            }
            tokenNum+=collection.size();
        }

        {
            countCharMap();
            keyNameSet.addAll(KeyWordCollection);
            keyNameSet.addAll(markCollection);
        }
        if(tokenNum!=keyNameSet.size()){
            throw new TokenisRepeat();
        }

        keyNameSet.add("ε");
    }

    public Lex getLex(){
        return new Lex(new IdentifierSetter(anno,mark,key),new LexRule(charMap));
    }

    /**
     * 生成Parser
     * @return
     * @throws GrammerUndefined
     * @throws IOException
     */
    public void ParserGenerate() throws GrammerUndefined, IOException, LeftCommonFactorConflict, FollowDebugException {
        for(String filename:grammerFileList){
            //遍历grammer.list中记录的文件
            //传递到makeRule中
            makeRule(grammerReader.makeConbinationGrammer(grammerReader.getLinesinFile(filename+".grammer")));
        }

        toCountCollection=new LinkedList<>(ruleNameSet);
        TraverseNonTerminals();
        countFollowCollection();

        analysisTable=makeMap();
    }

    public Parser getParser() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return new Parser(analysisTable);
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
//            if(!prop.getTerminalStructure()) {
                makeBranchTreeNode node=new makeBranchTreeNode(prop);
                node.AnalysisChildPropClass();
                javaFiles.add(node.buildFile());
//            }
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
        for(String nonterminal: ruleNameSet){
            nonTerminalMarkInfo ri=ruleMap.get(nonterminal);

            for(Rule rule:ri.getRules()){
                Set<String> tempset;
                if(ruleNameSet.contains(rule.getFirstMark())){
                    //这个地方有漏洞，如果该条产生式的首非终结符的first集合中存在ε，则tempset缺少follow集合
                    //目前已经通过select集填补上
                    tempset=ruleMap.get(rule.getFirstMark()).getSelectSet();
                }
                else if(rule==Rule.epsilon){
                    tempset=ri.getFollowSet();
                }
                else{
                    ptable.setDriverTable(rule.getFirstMark(), nonterminal, rule, ruleMap);
                    continue;
                }

                for (String terminal : tempset) {
                    ptable.setDriverTable(terminal, nonterminal, rule, ruleMap);
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
                ruleMap.put(e.getKey(), new nonTerminalMarkInfo());
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

    private void TraverseNonTerminals(){
        while(!toCountCollection.isEmpty()){
            countFirstCollection(toCountCollection.getFirst(),new HashSet<>());
        }
    }
    /**
     * 计算一个终结符的first集
     */
    private void countFirstCollection(String s,Set<String> stopValue){
        toCountCollection.remove(s);
        if(stopValue.contains(s)){
            return;
        }

        nonTerminalMarkInfo ri=ruleMap.get(s);
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

    /**
     * 计算follow集和select集
     */
    private void countFollowCollection() throws FollowDebugException {
        ruleMap.get("S").addTerminaltoFollowSet("#");
        FollowCollectionMaker maker=new FollowCollectionMaker(ruleMap,ruleNameSet);
        ruleMap=maker.countFollowCollection();

        for(nonTerminalMarkInfo ri:ruleMap.values()){
            ri.makeSelectSet();
        }
    }


    private void countCharMap() throws LexRuleException {
        LexStructureAnalysis lsa=new LexStructureAnalysis();
        this.charMap=lsa.AnalysisFunction(markCollection,KeyWordCollection);
        KeyWordCollection=lsa.getAnalysiskeywordSet();
    }
}
