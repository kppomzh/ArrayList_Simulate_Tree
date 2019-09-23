package GrammarMaker;

import Exceptions.GrammerMakerError.GrammerUndefined;
import Exceptions.GrammerMakerError.LeftCommonFactorConflict;
import Exceptions.GrammerMakerError.TokenisRepeat;
import Lex.Lex;
import Parser.Parser;
import Utils.JavaPoet.makeBranchTreeNode;
import bean.GrammerMaker.RuleInfo;
import bean.KVEntryImpl;
import bean.Lex.IdentifierSetter;
import bean.Parser.PredictiveAnalysisTable;
import bean.Parser.Rule;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static sun.tools.jconsole.Messages.CLASS_PATH;

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

    public static void main(String[] args) throws IOException, TokenisRepeat, GrammerUndefined, LeftCommonFactorConflict, ClassNotFoundException {
        Reader reader=new Reader("./");
        Lex lex=reader.LexGenerate();
        Parser parser=reader.ParserGenerate();
        reader.ASTGenerate();

        ObjectOutputStream lexStream=new ObjectOutputStream(new FileOutputStream(new File("lex.grammarclass")));
        ObjectOutputStream parserStream=new ObjectOutputStream(new FileOutputStream(new File("parser.grammarclass")));

        lexStream.writeObject(lex);
        parserStream.writeObject(parser);
        lex=null;
        parser=null;

        ObjectInputStream lexinput=new ObjectInputStream(new FileInputStream(new File("lex.grammarclass")));
        ObjectInputStream parserinput=new ObjectInputStream(new FileInputStream(new File("parser.grammarclass")));

        lex=(Lex)lexinput.readObject();
        parser=(Parser)parserinput.readObject();

        if(lex!=null&&parser!=null){
            System.out.println(true);
        }
        else{
            System.out.println(false);
        }
    }

    /**
     * @param files
     * java文件实时编译demo，以后可以据此修改方法
     */
    private static void javaCompires(File... files) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(files);

        ArrayList<String> ops = new ArrayList<String>();
        ops.add("-classpath");
        ops.add(CLASS_PATH);

        JavaCompiler.CompilationTask task =compiler.getTask(null, manager, null, ops, null, it);
        task.call();
    }

    /**
     * @param classname
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * java文件动态运行demo，以后可以据此修改方法
     */
    private static void javaRunner(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        //获取类加载器
        ClassLoader classLoader = Class.class.getClassLoader();
        Class<?> cls = classLoader.loadClass(classname);
        Object obj = cls.newInstance();

        //调用方法名称
        String methodName = "addChild";
        //方法参数类型数组
        Class<?>[] paramCls = {};
        //获取方法
        Method method = cls.getDeclaredMethod(methodName , paramCls);

        //方法参数
        Object[] params = {""};
        //调用方法
        Object result = method.invoke(obj, params);
    }

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
    public void ASTGenerate() throws ClassNotFoundException {
        for (String nodeName:ruleNameSet){
            makeBranchTreeNode node=new makeBranchTreeNode(nodeName);
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

            r.setRules(new LinkedList<>(List.of(rules)));
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
    private void countFollowCollection(){
    }
}
