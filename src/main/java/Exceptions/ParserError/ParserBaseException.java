package Exceptions.ParserError;

import bean.Word;

public abstract class ParserBaseException extends Exception {
    protected StringBuilder errMessage;
    protected ParserBaseException(String word,int line,int list){
        errMessage=new StringBuilder();
        errMessage.append(word);
        errMessage.append(" at line:");
        errMessage.append(line);
        errMessage.append(",list:");
        errMessage.append(list);
        errMessage.append("  ");
    }
    
    @Override
    public String getMessage(){
        return errMessage.toString();
    }
}
