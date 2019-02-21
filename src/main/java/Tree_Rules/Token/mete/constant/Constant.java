package Tree_Rules.Token.mete.constant;

import Tree_Rules.Token.mete.Mete;

//常量
public abstract class Constant extends Mete {
    //每个常量都需要标注长度，方便进行隐式转换和检查
    protected int[] range;

    public Constant(String tokenName, String meteName, int[] ranges) {
        super(tokenName, meteName);
        this.range=ranges;
    }

    public abstract boolean setConstant(String constantString);

    public abstract Object getConstant();
}
