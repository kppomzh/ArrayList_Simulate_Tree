package bean.Parser;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 文法表达式在分析程序中对应的实体
 */
public class Rule implements Serializable {
    /**
     * 规则名称
     */
    protected String RuleName;
    private LinkedList<String> rules;

    public Rule(String ruleName){
        this.RuleName=ruleName;
    }

    public String getRuleName() {
        return RuleName;
    }

    public LinkedList<String> getRules() {
        return rules;
    }

    public void setRules(LinkedList<String> rules) {
        this.rules = rules;
    }

    public int length(){
        return rules.size();
    }
}
