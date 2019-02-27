package Tree_Rules.Token.mete.constant.ConstantImpl;

public class ConstantString extends Tree_Rules.Token.mete.constant.Constant {
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
