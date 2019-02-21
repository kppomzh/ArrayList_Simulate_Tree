package Tree_Rules.Token.mete;

import Tree_Rules.RuleImpl;
import Tree_Rules.rule;

public abstract class Mete extends RuleImpl {
    //该变量或常量的名称
    protected String meteName;

    public Mete(String tokenName, String meteName) {
        super(tokenName);
        this.meteName=meteName;
    }

    public String getMeteName(){
        return meteName;
    }

    @Override
    public String[] getBaseToken(){
        return new String[]{RuleName};
    }

    @Override
    public rule getRule(rule r) {
        if(this.equals(r))
            return this;
        return null;
    }

    @Override
    public rule getRule(String prefix) {
        if(RuleName.equals(prefix))
            return this;
        return null;
    }
}
