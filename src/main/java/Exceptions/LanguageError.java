package Exceptions;

public abstract class LanguageError extends Exception{
    protected int line, list;
    protected String detailMessage;

    public LanguageError(int line1, int list1){
        line = line1;
        list = list1;
    }

    @Override
    public String getMessage(){
        return detailMessage;
    }
}
