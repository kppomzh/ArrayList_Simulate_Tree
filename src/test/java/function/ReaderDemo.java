package function;

import Exceptions.GrammerMakerError.GrammerBaseException;
import Exceptions.GrammerMakerError.Impl.FollowDebugException;
import Exceptions.GrammerMakerError.Impl.GrammerUndefined;
import Exceptions.LexError.LexBaseException;
import Exceptions.ParserError.ParserBaseException;
import GrammarMaker.Reader;
import Lex.Lex;
import Parser.Parser;
import Tree_Span.BranchTreeRoot;
import Utils.RunampCompileASTClasses;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

//import static sun.tools.jconsole.Messages.CLASS_PATH;

public class ReaderDemo {
    @Test
    public void main() throws IOException, GrammerBaseException, GrammerUndefined, ClassNotFoundException, FollowDebugException, IllegalAccessException, InvocationTargetException, InstantiationException, LexBaseException, ParserBaseException {
        Reader reader=new Reader("D:\\Document\\OneDrive\\CodeRepo\\ASL数据库项目\\ArrayList_Simulate_Tree\\");
        reader.LexGenerate();
        reader.ParserGenerate();
        List<File> javaFiles = reader.ASTGenerate();
        RunampCompileASTClasses.getInstance().javaCompile(javaFiles.toArray(new File[0]));

        Lex lex=reader.getLex();
        Parser parser=reader.getParser();

        ObjectOutputStream lexStream=new ObjectOutputStream(new FileOutputStream(new File("lex.grammarclass")));
        ObjectOutputStream parserStream=new ObjectOutputStream(new FileOutputStream(new File("parser.grammarclass")));

        lexStream.writeObject(lex);
        parserStream.writeObject(parser);

        BranchTreeRoot root=parser.Controller(lex.getWords("1+abs(6*8/7)"));
//        System.out.println(LanguageTreePrinter.printf(root,0));

        ObjectInputStream lexinput=new ObjectInputStream(new FileInputStream(new File("lex.grammarclass")));
        ObjectInputStream parserinput=new ObjectInputStream(new FileInputStream(new File("parser.grammarclass")));

        System.out.println("lex sample " + lex.equals(lexinput.readObject()));
        System.out.println("parser sample " + parser.equals(parserinput.readObject()));
    }

    @Test
    public void runSQL() throws IOException, ClassNotFoundException, LexBaseException, InstantiationException, IllegalAccessException, ParserBaseException, InvocationTargetException {
        ObjectInputStream lexinput=new ObjectInputStream(new FileInputStream(new File("lex.grammarclass")));
        ObjectInputStream parserinput=new ObjectInputStream(new FileInputStream(new File("parser.grammarclass")));

        Lex lex=(Lex)lexinput.readObject();
        Parser parser=(Parser)parserinput.readObject();

        BranchTreeRoot root=parser.Controller(lex.getWords("1+abs(6*8/7)"));
        System.out.println(root.getBranchName());
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
