package bean.Lex;

import java.util.*;

public class IdentifierSetter {
//    private String[] identifierArray={"create","drop","select","insert","update","delete",
//            "table","tablespace","from","where","union","set","into","values",
//            "int","double","string",//用于标识符
//            "Integer","Double","String",//用于标记常量
//            "NormalTAG",//普通对象名称，可能是变量、对象名、包函数名等。
//            "alter","add","del","column",
//            "and","or","in",
//            "null","is","not",
//            "sqrt","abs","pow",
//            "order","group","by","desc","asc",
//            "commit"
//            ,"show","cpu","dbtree","memory" //软件运行情况以及表目录结构
//    };
//    private String[] markArray={"+","-","*","/","^","%",
//            "=",":=","==",">",">=","<","<=","!=",".",
//            ",","(",")","\'","&","|"
//    };
//    private String[] annotationArray={"--","/*","*/"}; //注释符号

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
