package Tree_Rules;

import java.util.HashMap;

/**
 * 带分支的语法分支
 */
public class Rule_Tree extends RuleImpl {
    private HashMap<String,rule> childRuleList_FirstColle;
    public Rule_Tree(String ruleName)
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
        rule Rule=childRuleList_FirstColle.get(prefix);
        Rule.setParent(this);
        return Rule;
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
