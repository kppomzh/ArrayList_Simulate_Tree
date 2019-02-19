package Tree_Rules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Rule_tree implements rule{
//    private LinkedList<rule> childRuleTree=new LinkedList<rule>();
    private String classname;
    private HashMap<String,rule> childRuleList_FirstColle;
    public Rule_tree(String classname)
    {
        this.classname=classname;
    }

    @Override
    public String getClassname() {
        return classname;
    }

    @Override
    public rule getRule(rule r) {
        return null;
    }

    public void add_Child_Rule(rule childRule) {

    }
}
