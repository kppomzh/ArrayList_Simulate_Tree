package Exceptions.LexError.Impl;

import Exceptions.LexError.LexBaseException;

/**
 * 非法字符错误
 */
public class InvalidSymbolException extends LexBaseException {
    public InvalidSymbolException(int line,int list,char... word) {
        super(line,list,String.valueOf(word));
        errMessage.append("Decimal point is repeated~!");
    }
}
