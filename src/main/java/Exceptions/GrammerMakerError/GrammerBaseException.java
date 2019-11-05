package Exceptions.GrammerMakerError;

public abstract class GrammerBaseException extends Exception {
    private String detailMessage;
    private StringBuilder errMessage;
    boolean detail;

    public GrammerBaseException(String msg){
        detailMessage=msg;
        detail=true;
    }

    public GrammerBaseException(){
        errMessage=new StringBuilder("Serious Error:");
        detail=false;
    }

    protected void append(String msg){
        errMessage.append(msg);
    }
    protected void append(Number msg){
        errMessage.append(msg);
    }

    public String getMessage(){
        return detail?detailMessage:errMessage.toString();
    }
}
