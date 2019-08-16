package bean.Lex;

import java.util.*;

public class IdentifierSetter {
    private static String[] identifierArray={"create","drop","select","insert","update","delete",
            "table","tablespace","from","where","union","set","into","values","alter","add","del",
            "int","double","string",//用于标识符
            "Integer","Double","String",//用于标记常量
            "NormalTAG",//普通对象名称，可能是变量、对象名、包函数名等。
            "column","TableName","ListName","TablespaceName","CursorName","SequenceName",
            "newTableName","newListName",
            "and","or","in",
            "null","is","not",
            "sqrt","abs","pow",
            "commit"
            ,"show","cpu","dbtree","memory" //软件运行情况以及表目录结构
    };
    private static String[] markArray={"+","-","*","/","^","%",
            "=",":=","==",">",">=","<","<=","!=",
            ",","(",")","\'"//,";"
    };
    private static String[] annotationArray={"--","/*","*/"}; //注释符号

    private static final Set<String> identifier= Set.of(identifierArray);
    private static final Set<String> mark= Set.of(markArray);
    private static final Set<String> annotation=Set.of(annotationArray);

    public static boolean isMark(String s){
        return mark.contains(s)||isAnnotation(s);
    }
    public static boolean isIdentifier(String s){
        return identifier.contains(s);
    }
    public static boolean isAnnotation(String s){
        return annotation.contains(s);
    }

    public static boolean isKeyword(String s){
        return isIdentifier(s)||isMark(s);
    }
}
