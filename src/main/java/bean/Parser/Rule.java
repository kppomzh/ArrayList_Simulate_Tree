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
//    protected String RuleName;
    private List<String> rules;

    public Rule(){
//        this.RuleName=ruleName;
    }

    public String getFirstMark() {
        return rules.get(0);
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public int length(){
        return rules.size();
    }
}
