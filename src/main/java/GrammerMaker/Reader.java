package GrammerMaker;

import Exceptions.GrammerMakerError.GrammerUndefined;
import Exceptions.GrammerMakerError.TokenisRepeat;
import Lex.Lex;
import Parser.Parser;
import bean.GrammerMaker.RuleInfo;
import bean.GrammerMaker.RuleTree;
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
    private HashMap<String, RuleTree> ruleTreeMap;
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

        ruleTreeMap=new HashMap<>();
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
    public Parser ParserGenerate() throws GrammerUndefined, IOException {
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
    public void ASTGenerate(){

    }

    /**
     * 根据非终结符信息构造分析表
     * @return
     */
    public PredictiveAnalysisTable makeMap() {
        PredictiveAnalysisTable ptable=new PredictiveAnalysisTable(keyNameSet,ruleNameSet);
        for(String rulename: ruleMap.keySet()){
            String nonterminal=rulename;
            RuleInfo ri=ruleMap.get(nonterminal);

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

            r.setRules(new LinkedList<>(List.of(rules)));
            if(ruleTreeMap.containsKey(e.getKey())){
                ruleTreeMap.get(e.getKey()).addFirstSet(rules[0]);
                ruleMap.get(e.getKey()).addRule(r);
            }
            else{
                RuleTree tree=new RuleTree();
                tree.addFirstSet(rules[0]);
                ruleTreeMap.put(e.getKey(),tree);

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
        Set<String> set=ruleTreeMap.get(s).getFirstSet();
        for(String child:set){
            if(keyNameSet.contains(s)){

            }
        }
    }

    /**
     * 计算follow集
     */
    private void countFollowCollection(){
    }
}
