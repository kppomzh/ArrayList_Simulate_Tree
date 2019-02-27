package Tree_Rules.Token.mete.constant.ConstantImpl;

import java.math.BigInteger;

public class ConstantInteger extends Tree_Rules.Token.mete.constant.Constant {
    private BigInteger Constant;

    public ConstantInteger(String meteName, int[] ranges){
        super("Integer",meteName,ranges);
    }

    @Override
    public boolean setConstant(String constantString) {
        if(constantString.length()>this.range[0]){
            return false;
        }
        Constant=new BigInteger(constantString);
        return true;
    }

    @Override
    public BigInteger getConstant() {
        return Constant;
    }
}
