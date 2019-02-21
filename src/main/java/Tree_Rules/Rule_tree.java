package Tree_Rules;

import java.util.HashMap;

public class Rule_tree extends RuleImpl {
    private HashMap<String,rule> childRuleList_FirstColle;
    public Rule_tree(String ruleName)
    {
        super(ruleName);
        childRuleList_FirstColle=new HashMap<>();
    }

    @Override
    public rule getRule(rule r) {
        return null;
    }

    @Override
    public rule getRule(String prefix) {
        return childRuleList_FirstColle.get(prefix);
    }

    @Override
    public String[] getBaseToken() {
        return childRuleList_FirstColle.keySet().toArray(new String[0]);
    }

    public void add_Child_Rule(rule childRule) {
        //首符集需要向下一直找到单词为止
        for(String token:childRule.getBaseToken())
            childRuleList_FirstColle.put(token,childRule);
    }
}
