package Exceptions.GrammerMakerError.Impl;

import Exceptions.GrammerMakerError.GrammerBaseException;
import bean.Parser.Rule;

public class LeftCommonFactorConflict extends GrammerBaseException {
    private String detailMessage;

    public LeftCommonFactorConflict(String terminal,String nonterminal, Rule rule1,Rule rule2){
        super();
        append("对于非终结符");
        append(terminal);
        append("遇到终结符");
        append(nonterminal);
        append("时，存在以下两条产生式与其对应，分别是terminal->");
        for (int i = 0; i < rule1.getRules().size(); i++) {
            append(rule1.getRules().get(i));
            append(" ");
        }
        append("与terminal->");
        for (int i = 0; i < rule2.getRules().size(); i++) {
            append(rule2.getRules().get(i));
            append(" ");
        }
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
