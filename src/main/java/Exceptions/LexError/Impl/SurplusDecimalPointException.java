package Exceptions;

/**
 * 小数点在小数中过多
 */
public class SurplusDecimalPointException extends LanguageError {
    public SurplusDecimalPointException(int line1, int list1) {
        super(line1, list1);

        detailMessage="The decimal point at ("+line+','+list+") is surplus~!";
    }
}
