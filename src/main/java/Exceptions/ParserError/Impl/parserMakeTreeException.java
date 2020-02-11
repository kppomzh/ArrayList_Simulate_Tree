package Exceptions.ParserError.Impl;

import Exceptions.ParserError.ParserBaseException;
import bean.Word;

public class parserMakeTreeException extends ParserBaseException {
    public parserMakeTreeException(String message, Word word) {
        super(word.getName(),word.getStayline(),word.getStaylist());
        errMessage.append("\n");
        errMessage.append(message);
    }
}
