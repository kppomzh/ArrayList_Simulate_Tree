package Tree_Rules.Token;

import Tree_Rules.rule;

public class tokenIum implements rule {
    private String tokenName;

    public tokenIum(String tokenName) {
        this.tokenName = tokenName;
    }

    @Override
    public String getClassname() {
        return tokenName;
    }

    @Override
    public rule getRule(rule r) {
        if(this.equals(r))
            return this;
        return null;
    }

    public boolean equals(Object o){
        if(o==null)
            return false;

        if(!o.getClass().getName().equals(this.getClass().getName()))
            return false;

        if(!((tokenIum)o).tokenName.equals(this.tokenName))
            return false;
        return true;
    }
}
