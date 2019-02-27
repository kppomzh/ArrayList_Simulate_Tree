package Tree_Rules.Token.mete.constant.ConstantImpl;

import java.math.BigDecimal;

//常数
public class ConstantDecimal extends Tree_Rules.Token.mete.constant.Constant {
    private int intrange,decrange;
    private BigDecimal Constant;

    //通过这个tokenName判断number是属于什么类型的
    public ConstantDecimal(String meteName, int[] ranges){
        super("Decimal",meteName,ranges);
        intrange=ranges[0];
        decrange=ranges[1];
    }



    public int[] getRange(){
        return new int[]{intrange,decrange};
    }

    @Override
    public BigDecimal getConstant() {
        return Constant;
    }

    @Override
    public boolean setConstant(String constantString) {
        String[] dec=constantString.split("\\.");
        if(dec[0].length()>range[0]){
            return false;
        }
        if(dec.length>1){
            if(dec[1].length()>range[1]){
                return false;
            }
        }
        Constant=new BigDecimal(constantString);
        return true;
    }
}
