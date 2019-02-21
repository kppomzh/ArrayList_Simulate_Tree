package Tree_Rules.Token.mete.constant.ConstantImpl;

import Tree_Rules.Token.mete.constant.Constant;

public class ConstantString extends Constant {
    private String Constant;

    public ConstantString(String meteName, int[] ranges) {
        super("String", meteName,ranges);
    }

    @Override
    public boolean setConstant(String constantString) {
        if(constantString.length()>range[0]){
            return false;
        }
        Constant=constantString;
        return true;
    }

    @Override
    public String getConstant() {
        return Constant;
    }
}
