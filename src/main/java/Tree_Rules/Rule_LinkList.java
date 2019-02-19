package Tree_Rules;

import java.util.Iterator;
import java.util.LinkedList;

public class Rule_LinkList {
    private LinkedList<rule> lineofrule=new LinkedList<rule>();

    public void add_Rule(rule r)
    {
        lineofrule.add(r);
    }

    public Iterator<rule> getLineofrule() {
        return lineofrule.listIterator();
    }
}
