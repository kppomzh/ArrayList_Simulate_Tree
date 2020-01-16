package bean.Parser;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 文法产生式在分析程序中对应的实体
 * 仅包含产生式右部
 */
public class Rule implements Serializable {
    public final static Rule epsilon;

    static {
        epsilon = new Rule();
        epsilon.setRules(List.of("ε"));
    }

    private List<String> rules;

    public Rule() {

    }

    public String getFirstMark() {
        return rules.get(0);
    }

    public String getLastMark() {
        return rules.get(rules.size() - 1);
    }

    public String getRuleIndex(int index) {
        return rules.get(index);
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public int length() {
        return rules.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().getName().equals(this.getClass().getName())) return false;
        Rule r = (Rule) o;

        return r.rules.equals(this.rules);
    }
//
//    @Override
//    public int hashCode(){
//
//    }
}
