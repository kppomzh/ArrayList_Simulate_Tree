package Tree_Rules;

import java.util.HashMap;

public abstract class RuleImpl implements rule {
    /**
     * 规则名称
     */
    protected String RuleName;
    /**
     * 规则的父规则集合，暂时无用
     */
    private HashMap<String,rule> parents;
    /**
     * 规则的父规则，通过set方法设置。
     */
    protected rule parent;

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
    public void setParent(rule parentRule){
        parent=parentRule;
    }

    @Override
    public rule getParent(){
        return parent;
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
