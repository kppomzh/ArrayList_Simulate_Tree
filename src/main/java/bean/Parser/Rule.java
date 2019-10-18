package bean.Parser;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 文法表达式在分析程序中对应的实体
 */
public class Rule implements Serializable {
    public final static Rule epsilon;
    static {
        epsilon=new Rule();
        epsilon.setRules(List.of("ε"));
    }

    private List<String> rules;

    public Rule(){

    }

    public String getFirstMark() {
        return rules.get(0);
    }

    public String getLastMark() {
        return rules.get(rules.size()-1);
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
