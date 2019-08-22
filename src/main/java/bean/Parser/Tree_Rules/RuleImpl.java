package bean.Parser.Tree_Rules;

import java.util.LinkedList;

/**
 * 文法在分析程序中对应的实体
 */
public abstract class RuleImpl {
    /**
     * 规则名称
     */
    protected String RuleName;
    private LinkedList<String> rules;

    public RuleImpl(String ruleName){
        this.RuleName=ruleName;
    }

    public String getRuleName() {
        return RuleName;
    }

    protected LinkedList<String> getRules() {
        return rules;
    }

    protected void setRules(LinkedList<String> rules) {
        this.rules = rules;
    }

    public int length(){
        return rules.size();
    }
}
