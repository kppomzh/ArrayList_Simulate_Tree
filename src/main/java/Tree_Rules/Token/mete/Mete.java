package Tree_Rules.Token.mete;

import Tree_Rules.Token.TokenIum;

public abstract class Mete extends TokenIum {
    //该变量或常量的名称
    protected String meteName;

    public Mete(String tokenName, String meteName) {
        super(tokenName);
        this.meteName=meteName;
    }

    public String getMeteName(){
        return meteName;
    }
}
