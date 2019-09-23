package bean.Lex;

import java.io.Serializable;
import java.util.*;

public class IdentifierSetter implements Serializable {
    private ArrayList<String> identifier;
    private ArrayList<String> mark;
    private ArrayList<String> annotation;

    public IdentifierSetter(String[] annotationArray,String[] markArray,String[] identifierArray){
        identifier=new ArrayList(List.of(identifierArray));
        mark= new ArrayList(List.of(markArray));
        annotation=new ArrayList(List.of(annotationArray));
    }

    public boolean isMark(String s){
        return mark.contains(s)||isAnnotation(s);
    }
    public boolean isIdentifier(String s){
        return identifier.contains(s);
    }
    public boolean isAnnotation(String s){
        return annotation.contains(s);
    }

    public boolean isKeyword(String s){
        return isIdentifier(s)||isMark(s);
    }
}
