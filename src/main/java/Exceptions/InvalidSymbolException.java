package Exceptions;

/**
 * 非法字符错误
 */
public class InvalidSymbolException extends LanguageError {
    private char c;

    public InvalidSymbolException(int line1, int list1, char c1) {
        super(line1,list1);
        c = c1;

        detailMessage="The char \'"+c+"\' at ("+line+','+list+") is an Invalid Symbol~!";
    }
}
