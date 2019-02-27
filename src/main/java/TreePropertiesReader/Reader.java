package TreePropertiesReader;

import Tree_Rules.Rule_LinkList;
import Tree_Rules.Rule_Tree;
import Tree_Rules.Token.TokenIum;
import Tree_Rules.rule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class Reader {
    private Properties ruleProperties = new Properties();
    private HashMap<String, rule> ruleMap = new HashMap<>();

    public void loadRules() throws IOException {
        ruleProperties.load(new FileInputStream(new File("")));
    }

    public HashMap<String, rule> makeMap() {
        for(Object ruleName:ruleProperties.keySet()){
            makeRule((String) ruleName);
        }

        return ruleMap;
    }

    private void makeRule(String ruleName) {
        String[] treeOlist;
        if (ruleProperties.getProperty(ruleName) == null) {
            ruleMap.put(ruleName, new TokenIum(ruleName));
            ruleProperties.remove(ruleName);
        } else if (ruleProperties.getProperty(ruleName).indexOf("|") != -1) {
            Rule_Tree rule = new Rule_Tree(ruleName);
            treeOlist = ruleProperties.getProperty(ruleName).split("|");
            for (String s :treeOlist){
                if(!ruleMap.containsKey(s)){
                    makeRule(s);
                }
                rule.add_Child_Rule(ruleMap.get(s));
            }

        }
        else if(ruleProperties.getProperty(ruleName).indexOf(" ") != -1){
            Rule_LinkList rule =new Rule_LinkList(ruleName);
            treeOlist= ruleProperties.getProperty(ruleName).split(" ");
            for (String s :treeOlist){
                if(!ruleMap.containsKey(s)){
                    makeRule(s);
                }
                rule.add_Rule(ruleMap.get(s));
            }
        }

    }
}
