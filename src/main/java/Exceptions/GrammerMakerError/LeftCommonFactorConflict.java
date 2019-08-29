package Exceptions.GrammerMakerError;

import bean.Parser.Rule;

public class LeftCommonFactorConflict extends Exception {
    private String detailMessage;

    public LeftCommonFactorConflict(String terminal,String nonterminal, Rule rule1,Rule rule2){
        StringBuilder sb=new StringBuilder("对于非终结符");
        sb.append(terminal);
        sb.append("遇到终结符");
        sb.append(nonterminal);
        sb.append("时，存在以下两条产生式与其对应，分别是terminal->");
        for (int i = 0; i < rule1.getRules().size(); i++) {
            sb.append(rule1.getRules().get(i));
            sb.append(' ');
        }
        sb.append("与terminal->");
        for (int i = 0; i < rule2.getRules().size(); i++) {
            sb.append(rule2.getRules().get(i));
            sb.append(' ');
        }

        detailMessage=sb.toString();
    }

    @Override
    public String getMessage(){
        return detailMessage;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
