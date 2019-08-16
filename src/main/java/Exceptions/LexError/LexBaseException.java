package Exceptions.LexError;
import bean.Word;

public abstract class LexBaseException extends Exception {
    protected StringBuilder errMessage;
    protected LexBaseException(Word word){
        errMessage=new StringBuilder();
        errMessage.append(word.getSubstance());
        errMessage.append(" at line:");
        errMessage.append(word.getStayline());
        errMessage.append(",list:");
        errMessage.append(word.getStaylist());
        errMessage.append("  ");
    }

    protected LexBaseException(int line,int list,String word){
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
