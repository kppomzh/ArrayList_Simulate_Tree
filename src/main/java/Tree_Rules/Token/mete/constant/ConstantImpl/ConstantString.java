package Tree_Rules.Token.mete.constant.ConstantImpl;

import Tree_Rules.Token.mete.constant.ConstantBase;

public class ConstantString extends ConstantBase {
    private String ConstantBase;

    public ConstantString(String meteName, int[] ranges) {
        super("String", meteName,ranges);
    }

    @Override
    public boolean setConstant(String constantString) {
        if(constantString.length()>range[0]){
            return false;
        }
        ConstantBase =constantString;
        return true;
    }

    public String getConstant() {
        return ConstantBase;
    }
}
