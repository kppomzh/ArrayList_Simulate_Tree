package GrammerMaker;

import Exceptions.GrammerMakerError.GrammerUndefined;
import Exceptions.GrammerMakerError.TokenisRepeat;
import Lex.Lex;
import Parser.Parser;
import bean.KVEntryImpl;
import bean.Lex.IdentifierSetter;
import bean.Parser.PredictiveAnalysisTable;
import bean.Parser.Rule;

import java.io.*;
import java.util.*;

public class Reader {
    private static final String[] FixLoadGrammer={"annotation","keyword","mark"};
    private int tokenNum=0;
    private String basePath;
    private File grammer;
    private List<File> grammerFileList;
    private List<KVEntryImpl<String, Rule>> ruleList;
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

        ruleList = new ArrayList<>();
    }

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

    public Parser ParserGenerate() throws GrammerUndefined, IOException {
        for(File file:grammerFileList){
            List<KVEntryImpl<String,String>> prop=new LinkedList<>();
            FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);

            while(br.ready()){
                String rule=br.readLine().strip();
                String[] Production=rule.split(":");
                if(Production.length!=2){
                    throw new GrammerUndefined("该文法的产生式格式不正确~！");
                }
                prop.add(new KVEntryImpl<>(Production[0].strip(),Production[1].strip()));
            }
            makeRule(prop);
        }
        return new Parser(makeMap());
    }

    public PredictiveAnalysisTable makeMap() {
        PredictiveAnalysisTable ptable=new PredictiveAnalysisTable(keyNameSet,ruleNameSet);
        for(KVEntryImpl<String,Rule> kv:ruleList){
            String nonterminal=kv.getKey();
            Rule r=kv.getValue();

        }

        return ptable;
    }

    private void makeRule(List<KVEntryImpl<String,String>> prop) throws GrammerUndefined {
        for(KVEntryImpl<String,String> e:prop){
            ruleNameSet.add(e.getKey());
        }

        for(KVEntryImpl<String,String> e:prop){
            Rule r=new Rule(e.getKey());
            String[] rules=e.getValue().split(" ");

            for(String singalrule:rules){
                if(!(ruleNameSet.contains(singalrule)||keyNameSet.contains(singalrule))){
                    throw new GrammerUndefined("该文法单位的定义不存在~！");
                }
            }

            r.setRules(new LinkedList<>(List.of(rules)));
            ruleList.add(new KVEntryImpl<>(e.getKey(),r));
        }
    }
}
