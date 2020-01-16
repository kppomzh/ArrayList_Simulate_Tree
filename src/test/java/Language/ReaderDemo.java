package Language;

import Exceptions.GrammerMakerError.GrammerBaseException;
import Exceptions.GrammerMakerError.Impl.FollowDebugException;
import Exceptions.GrammerMakerError.Impl.GrammerUndefined;
import Exceptions.LexError.LexBaseException;
import Exceptions.ParserError.ParserBaseException;
import GrammarMaker.Reader;
import Lex.Lex;
import Parser.Parser;
import Tree_Span.BranchTreeRoot;
import Utils.LanguageTreePrinter;
import Utils.RunampCompileASTClasses;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ReaderDemo {
    private Lex analex;
    private Parser anaparser;

    public void Analysis(List<String> files) throws IOException, GrammerBaseException, GrammerUndefined, ClassNotFoundException, FollowDebugException, IllegalAccessException, InvocationTargetException, InstantiationException, LexBaseException, ParserBaseException {
        Reader reader=new Reader(files);
        reader.LexGenerate();
        reader.ParserGenerate();
        List<File> javaFiles = reader.ASTGenerate();
        RunampCompileASTClasses.getInstance().javaCompile(javaFiles.toArray(new File[0]));

        analex=reader.getLex();
        anaparser=reader.getParser();

        ObjectOutputStream lexStream=new ObjectOutputStream(new FileOutputStream(new File("lex.grammarclass")));
        ObjectOutputStream parserStream=new ObjectOutputStream(new FileOutputStream(new File("parser.grammarclass")));

        lexStream.writeObject(analex);
        parserStream.writeObject(anaparser);
    }

    public void runSQLandCheck(String SQL) throws IOException, ClassNotFoundException, LexBaseException, InstantiationException, IllegalAccessException, ParserBaseException, InvocationTargetException {
        ObjectInputStream lexinput=new ObjectInputStream(new FileInputStream(new File("lex.grammarclass")));
        ObjectInputStream parserinput=new ObjectInputStream(new FileInputStream(new File("parser.grammarclass")));

        Lex lex=(Lex)lexinput.readObject();
        Parser parser=(Parser)parserinput.readObject();

        BranchTreeRoot root=parser.Controller(lex.getWords(SQL));
        System.out.println(LanguageTreePrinter.printf(root,0));

        System.out.println("反序列化词法分析器的一致性：" + analex.equals(lexinput.readObject()));
        System.out.println("反序列化语法分析器的一致性：" + anaparser.equals(parserinput.readObject()));
    }

    /**
     * @param classname
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * java文件动态运行demo，以后可以据此修改方法
     */
    private static void javaRunner(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        //获取类加载器
        ClassLoader classLoader = Class.class.getClassLoader();
        Class<?> cls = classLoader.loadClass(classname);
        Object obj = cls.getConstructor().newInstance();

        //调用方法名称
        String methodName = "addChild";
        //方法参数类型数组
        Class<?>[] paramCls = {};
        //获取方法
        Method method = cls.getDeclaredMethod(methodName , paramCls);

        //方法参数
        Object[] params = {""};
        //调用方法
        Object result = method.invoke(obj, params);
    }
}
