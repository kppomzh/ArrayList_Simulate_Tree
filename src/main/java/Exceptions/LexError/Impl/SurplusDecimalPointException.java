package Exceptions.LexError.Impl;

import Exceptions.LexError.LexBaseException;

/**
 * 小数点在小数中过多
 */
public class SurplusDecimalPointException extends LexBaseException {
    public SurplusDecimalPointException(int line,int list) {
        super(line,list,".");
        errMessage.append("");
    }
}