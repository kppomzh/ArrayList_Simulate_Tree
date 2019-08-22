package Exceptions.ParserError;

import bean.Word;

public class ParserBaseException extends Exception {
    protected StringBuilder errMessage;
    protected ParserBaseException(Word word){
        errMessage=new StringBuilder();
        errMessage.append(word.getSubstance());
        errMessage.append(" at line:");
        errMessage.append(word.getStayline());
        errMessage.append(",list:");
        errMessage.append(word.getStaylist());
        errMessage.append("  ");
    }
    
    @Override
    public String getMessage(){
        return errMessage.toString();
    }
}
