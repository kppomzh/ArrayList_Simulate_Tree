package Exceptions.ASTError;

public class ASTBaseException extends Exception {
    protected StringBuilder errMessage;

    @Override
    public String getMessage(){
        return errMessage.toString();
    }
}
