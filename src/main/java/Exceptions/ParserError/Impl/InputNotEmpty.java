package Exceptions.ParserError.Impl;

import Exceptions.ParserError.ParserBaseException;
import bean.Word;

import java.util.List;

public class InputNotEmpty extends ParserBaseException {
    public InputNotEmpty(Word word, List<Word> stack) {
        super(word.getName(), word.getStayline(), word.getStaylist());
        errMessage.append("There are still unprocessed words in the input stream!\n");
        errMessage.append("Some remaining symbols:");
        for(Word w:stack){
            errMessage.append(w.getName());
            errMessage.append(" ,");
        }
        errMessage.deleteCharAt(errMessage.length()-1);
    }
}
