package Exceptions.GrammerMakerError.Impl;

import Exceptions.GrammerMakerError.GrammerBaseException;

public class LexRuleException extends GrammerBaseException {
    public LexRuleException(String error,String lexRule,Character mark) {
        super();
        super.append("The mark \'");
        super.append(mark);
        super.append('\'');
        super.append(error);
    }
}
