package Tree_Rules;

import java.util.HashMap;

public abstract class RuleImpl implements rule {
    //规则名称
    protected String RuleName;
    //规则的父规则集合
    private HashMap<String,rule> parents;

    public RuleImpl(String ruleName){
        this.RuleName=ruleName;
    }

    @Override
    public String getRuleName() {
        return RuleName;
    }

    @Override
    public abstract rule getRule(rule r);

    @Override
    public abstract rule getRule(String prefix);

    @Override
    public rule getParent(String RuleName){
        return parents.get(RuleName);
    }

    @Override
    public boolean equals(Object o){
        if(o==null)
            return false;

        if(!o.getClass().getName().equals(this.getClass().getName()))
            return false;

        return ((RuleImpl) o).RuleName.equals(this.RuleName);
    }
}
