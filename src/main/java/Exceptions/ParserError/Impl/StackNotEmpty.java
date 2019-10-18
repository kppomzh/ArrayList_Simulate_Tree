package Exceptions.ParserError.Impl;

import Exceptions.ParserError.ParserBaseException;
import bean.Word;

import java.util.List;

public class StackNotEmpty extends ParserBaseException {
    public StackNotEmpty(Word word, List<String> stack) {
        super(word.getName(), word.getStayline(), word.getStaylist());
        errMessage.append("Missing end symbol!\n");
        errMessage.append("There are some remaining symbols:");
        for(String s:stack){
            errMessage.append(s);
            errMessage.append(" ,");
        }
        errMessage.deleteCharAt(errMessage.length()-1);
    }
}
