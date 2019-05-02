package Tree_Rules.Token.mete;

import Tree_Rules.Token.TokenIum;

/**
 * 量词基本类
 */
public abstract class MeteBase extends TokenIum {
    //该变量或常量的名称
    protected String meteName;

    public MeteBase(String tokenName, String meteName) {
        super(tokenName);
        this.meteName=meteName;
    }

    public String getMeteName(){
        return meteName;
    }
}
