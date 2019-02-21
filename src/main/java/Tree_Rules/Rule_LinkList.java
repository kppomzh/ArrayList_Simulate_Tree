package Tree_Rules;

import java.util.Iterator;
import java.util.LinkedList;

public class Rule_LinkList extends RuleImpl {
    private LinkedList<rule> lineofrule=new LinkedList<>();

    public Rule_LinkList(String ruleName) {
        super(ruleName);
    }

    public void add_Rule(rule r)
    {
        lineofrule.add(r);
    }

    public Iterator<rule> getLineofrule() {
        return lineofrule.listIterator();
    }

    @Override
    public rule getRule(rule r) {
        return null;
    }

    @Override
    public rule getRule(String prefix) {
        if(lineofrule.get(0).getRule(prefix)!=null)
            return this;
        return null;
    }

    @Override
    public String[] getBaseToken() {
        return lineofrule.get(0).getBaseToken();
    }
}
