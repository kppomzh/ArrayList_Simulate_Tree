package bean.Parser.Tree_Rules;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 顺序的语法分支，即表达式，或者也可以称为非终结符
 */
public class Rule_LinkList extends RuleImpl {

    public Rule_LinkList(String ruleName) {
        super(ruleName);
    }

    public LinkedList<String> getRules(){
        return super.getRules();
    }

    public void setRules(LinkedList<String> rules){
        super.setRules(rules);
    }
}
