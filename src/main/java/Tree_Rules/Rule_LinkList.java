package Tree_Rules;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 顺序的语法分支
 */
public class Rule_LinkList extends RuleImpl {
    protected int nowRule=0;
    protected LinkedList<rule> lineofrule=new LinkedList<>();

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
        if(checkPrefix(prefix)){
            rule Rule=lineofrule.get(nowRule);
            Rule.setParent(this);
            nowRule++;
            return Rule;
        }
        //所有的循环结构都应该是可以跳过的
        else while(lineofrule.get(nowRule) instanceof Rule_Loop){
            nowRule++;
            if(checkPrefix(prefix)){
                rule Rule=lineofrule.get(nowRule);
                Rule.setParent(this);
                nowRule++;
                return Rule;
            }
        }
        return null;
    }

    /**
     * <p>防止在本条规则没有解析完的时候就退出解析。</p>
     */
    @Override
    public rule getParent(){
        if(nowRule<lineofrule.size())
            return null;
        return parent;
    }

    @Override
    public String[] getBaseToken() {
        return lineofrule.get(0).getBaseToken();
    }

    private boolean checkPrefix(String prefix){
        if(nowRule==lineofrule.size())
            return false;

        return lineofrule.get(nowRule).getRule(prefix)!=null;
    }
}
