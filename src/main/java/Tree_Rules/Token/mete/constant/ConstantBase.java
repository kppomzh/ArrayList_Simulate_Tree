package Tree_Rules.Token.mete.constant;

import Tree_Rules.Token.mete.MeteBase;

/**
 * 常量
 * 每个常量都需要标注长度，方便进行隐式转换和检查
 */
public abstract class ConstantBase extends MeteBase {
    protected int[] range;

    /**
     * @param tokenName null
     * @param meteName 指定该Constant的名称
     * @param ranges 指定变量长度
     */
    public ConstantBase(String tokenName, String meteName, int... ranges) {
        super(tokenName, meteName);
        this.range=ranges;
    }

    public abstract boolean setConstant(String constantString);

    public abstract Object getConstant();
}
