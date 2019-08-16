package Exceptions.LexError.Impl;

import Exceptions.LexError.LexBaseException;

public class TerminatorNotFoundException extends LexBaseException {
    public TerminatorNotFoundException(int line, int list,char... cs) {
        super(line, list, new String(cs));
        errMessage.append("未找到对应的终结符");
    }
}
