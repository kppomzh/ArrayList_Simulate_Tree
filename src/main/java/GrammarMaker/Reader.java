package GrammarMaker;

import Exceptions.GrammerMakerError.GrammerUndefined;
import Exceptions.GrammerMakerError.LeftCommonFactorConflict;
import Exceptions.GrammerMakerError.TokenisRepeat;
import Lex.Lex;
import Parser.Parser;
import Utils.JavaPoet.makeBranchTreeNode;
import bean.GrammerMaker.ListMatrix;
import bean.GrammerMaker.RuleInfo;
import bean.GrammerMaker.childNodeProperty;
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
    private static final String[] FixLoadGrammer={"annotation","keyword","mark"};
    private int tokenNum=0;
    private String basePath;
    private File grammer;
    private List<File> grammerFileList;
    /**
     * 记录文法符号的信息，包括产生式右部，first集，follow集
     */
    private HashMap<String, RuleInfo> ruleMap;
    /**
     * ruleTreeMap用于指示文法符号之间的上下级关系
     */
//    private HashMap<String, RuleTree> ruleTreeMap;
    /**
     * ruleNameSet记录非终结符存在性
     * keyNameSet记录终结符存在性
     */
    private Set<String> ruleNameSet,keyNameSet;

    public Reader(String basePath) throws IOException {
        this.basePath=basePath;
        grammer=new File(basePath+"grammer.list");
        grammerFileList=new LinkedList<>();
        FileReader fr=new FileReader(grammer);
        BufferedReader br=new BufferedReader(fr);
        ruleNameSet =new HashSet<>();
        keyNameSet=new HashSet<>();

        while(br.ready()){
            String s=br.readLine();
            if(s==null||s.length()==0){
                continue;
            }
            grammerFileList.add(new File(basePath+s+".grammer"));
        }

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
            Properties temp=new Properties();
            temp.load(new FileInputStream(new File(basePath+FixLoadGrammer[i]+".grammer")));
            switch (FixLoadGrammer[i]){
                case "annotation":
                    anno=temp.keySet().toArray(new String[0]);
                    break;
                case "keyword":
                    key=temp.keySet().toArray(new String[0]);
                    break;
                case "mark":
                    mark=temp.keySet().toArray(new String[0]);
                    break;
            }
            tokenNum+=temp.size();
            keyNameSet.addAll(temp.stringPropertyNames());
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
    public Parser ParserGenerate() throws GrammerUndefined, IOException, LeftCommonFactorConflict {
        for(File file:grammerFileList){
            //遍历grammer.list中记录的文件
            List<KVEntryImpl<String,String>> prop=new LinkedList<>();
            FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);

            while(br.ready()){
                //将其中每个文件按行读取
                String rule=br.readLine().strip();
                //每行用冒号分割，得到第一步的整体产生式
                String[] Production=rule.split(":");
                if(Production.length!=2){
                    throw new GrammerUndefined("该文法的产生式格式不正确~！");
                }
                prop.add(new KVEntryImpl<>(Production[0].strip(),Production[1].strip()));
            }
            //传递到makeRule中
            makeRule(prop);
        }

        countFirstCollection("S");
        countFollowCollection();

        return new Parser(makeMap());
    }

    /**
     * 生成位于Tree_Span.Impl包下的AST数据结构
     */
    public void ASTGenerate() throws ClassNotFoundException, IOException {
        for (String nodeName:ruleNameSet){
            //统计非终结符每条产生式的信息
            List<Rule> rules=ruleMap.get(nodeName).getRules();
            childNodeProperty[] prop;
            if(rules.size()==1){
                prop=new childNodeProperty[]{new childNodeProperty(nodeName)};
                for(String s:rules.get(0).getRules()){
                    prop[0].setTerminal(false);

                }
            }
            else{
                prop=new childNodeProperty[rules.size()];
                for (int i = 0; i < rules.size(); i++) {
                    for (int j = 0; j < rules.get(i).getRules().size(); j++) {
                        prop[i]=new childNodeProperty(nodeName);
                    }
                }
            }
            //根据统计信息生成AST类
            makeBranchTreeNode node=new makeBranchTreeNode(nodeName);
            node.AnalysisClass(prop);
            node.buildFile();
        }
    }

    /**
     * 根据非终结符信息构造分析表
     * @return
     */
    private PredictiveAnalysisTable makeMap() throws LeftCommonFactorConflict {
        PredictiveAnalysisTable ptable=new PredictiveAnalysisTable(keyNameSet,ruleNameSet);
        for(String rulename: ruleNameSet){
            String nonterminal=rulename;
            RuleInfo ri=ruleMap.get(nonterminal);

            for(Rule rule:ri.getRules()){
                for(String terminal:ruleMap.get(rule.getFirstMark()).getFirstSet()){
                    ptable.setDriverTable(terminal,nonterminal,rule);
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
            Rule r=new Rule();
            String[] rules=e.getValue().split(" ");

            for(String singalrule:rules){
                if(!(ruleNameSet.contains(singalrule)||keyNameSet.contains(singalrule))){
                    throw new GrammerUndefined("该文法单位的定义不存在~！");
                }
            }

            r.setRules(new ArrayList<>(List.of(rules)));
            //rulemap中未记录则新建RuleInfo类
            if(ruleMap.containsKey(e.getKey())){
                ruleMap.get(e.getKey()).addRule(r);
            }
            else{
                RuleInfo ri=new RuleInfo();
                ri.addRule(r);
                ruleMap.put(e.getKey(),ri);
            }
        }
    }

    /**
     * 计算first集
     */
    private void countFirstCollection(String s){
        for(String child:this.ruleNameSet){
            RuleInfo ri=ruleMap.get(child);
            while(ri.hasNext()){
                String first=ri.getNextRule().getFirstMark();
                if(this.keyNameSet.contains(first)){
                    ri.addFirstSet(first);
                }
                else {
                    if(ruleMap.get(first).getFirstSet().size()==0){
                        countFirstCollection(first);
                    }
                    ri.addFirstSet(ruleMap.get(first).getFirstSet());
                }
            }
        }
    }

    /**
     * 计算follow集
     */
    private void countFollowCollection() {
        //存储所有first&&ε∈first?follow:null->follow
        ListMatrix<String> lm = new ListMatrix<>();
        //记录在文法产生式尾部和产生式左部匹配的非终结符
        //存储所有follow->follow
        Map<String,Set<String>> sameNonTerminal=new HashMap<>();
        Set<String> nonDependents=new HashSet<>(this.ruleNameSet);
        HashMap<String,Set<String>> nodeDependents=new HashMap<>();
        for(String s:nonDependents){
            nodeDependents.put(s,new HashSet<>());
        }

        for(Map.Entry<String,RuleInfo> e:ruleMap.entrySet()){
            RuleInfo ri=e.getValue();
            for(Rule rule:ri.getRules()){
                int i=0;
                List<String> marks=rule.getRules();

                for(;i<marks.size()-1;i++){
                    if(ruleNameSet.contains(marks.get(i))){
                        if(marks.get(i).equals(marks.get(i+1))){
                            //不做任何事情，因为两个符号一样
                        }
                        else if(ruleNameSet.contains(marks.get(i+1))){
                            lm.addConnect(marks.get(i+1),marks.get(i));
                            nonDependents.remove(marks.get(i));
                            nodeDependents.get(marks.get(i)).add(marks.get(i+1));
                        }
                        else{
                            ruleMap.get(marks.get(i)).addTerminaltoFollowSet(marks.get(i+1));
                        }
                    }
                }
                //避免右递归下的无效传输
                if(ruleNameSet.contains(marks.get(i))&&!marks.get(rule.length()-1).equals(e.getKey())){
                    if(!sameNonTerminal.containsKey(e.getKey()))
                        sameNonTerminal.put(e.getKey(),new HashSet<>());
                    sameNonTerminal.get(e.getKey()).add(rule.getRules().get(i));
                    nonDependents.remove(rule.getRules().get(i));
                    nodeDependents.get(rule.getRules().get(i)).add(e.getKey());
                }
            }
        }

        for(String s:nonDependents){
            //首先从有依赖的节点列表中移除已经没有依赖的节点
            nodeDependents.remove(s);
            //如果这些节点出现在sameNonTerminal中，那么现在已经可以放心的将这个节点的follow集传递给sameNonTerminal中s对应的所有value
            if(sameNonTerminal.containsKey(s)){
                for(String chrule:sameNonTerminal.get(s)){
                    ruleMap.get(chrule).addFollowSet(ruleMap.get(s).getFollowSet());
                }
                sameNonTerminal.remove(s);
            }
            //如果从s到任意一个节点间存在向前的传递first&follow集的关系
            for(String chrule:ruleNameSet){
                if(lm.isConnect(s,chrule)){
                    //先添加first集到chrule的follow集
                    ruleMap.get(chrule).addFollowSet(ruleMap.get(s).getFirstSet());

                    //s的first集中是否有ε
                    if(ruleMap.get(s).isGiveFollow()){
                        //添加follow集到chrule的follow集
                        ruleMap.get(chrule).addFollowSet(ruleMap.get(s).getFollowSet());
                    }
                    //从chrule的依赖列表中拿走当前s
                    nodeDependents.get(chrule).remove(s);
                    if(nodeDependents.get(chrule).isEmpty()){
                        //如果依赖列表已经空了，那说明现在chrule的两个集合是稳定的，可以对外提供服务了
                        nonDependents.add(chrule);
                    }
                }
            }
        }

        //处理存在循环依赖的节点
        for(String s:nodeDependents.keySet()){

        }

        //处理完还有依赖的节点之后，就可以着手清理sameNonTerminal中剩余的follow集依赖
        for(Map.Entry<String,Set<String>> entry:sameNonTerminal.entrySet()){
            for(String chrule:entry.getValue()){
                ruleMap.get(chrule).addFollowSet(ruleMap.get(entry.getKey()).getFollowSet());
            }
        }
    }
}
