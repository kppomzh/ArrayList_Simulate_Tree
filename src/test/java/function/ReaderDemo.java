package function;

import Exceptions.GrammerMakerError.GrammerUndefined;
import Exceptions.GrammerMakerError.LeftCommonFactorConflict;
import Exceptions.GrammerMakerError.TokenisRepeat;
import GrammarMaker.Reader;
import Lex.Lex;
import Parser.Parser;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static sun.tools.jconsole.Messages.CLASS_PATH;

public class ReaderDemo {
    public static void main(String[] args) throws IOException, TokenisRepeat, GrammerUndefined, LeftCommonFactorConflict, ClassNotFoundException {
        Reader reader=new Reader("./");
        Lex lex=reader.LexGenerate();
        Parser parser=reader.ParserGenerate();
        reader.ASTGenerate();

        ObjectOutputStream lexStream=new ObjectOutputStream(new FileOutputStream(new File("lex.grammarclass")));
        ObjectOutputStream parserStream=new ObjectOutputStream(new FileOutputStream(new File("parser.grammarclass")));

        lexStream.writeObject(lex);
        parserStream.writeObject(parser);
        lex=null;
        parser=null;

        ObjectInputStream lexinput=new ObjectInputStream(new FileInputStream(new File("lex.grammarclass")));
        ObjectInputStream parserinput=new ObjectInputStream(new FileInputStream(new File("parser.grammarclass")));

        lex=(Lex)lexinput.readObject();
        parser=(Parser)parserinput.readObject();

        if(lex!=null&&parser!=null){
            System.out.println(true);
        }
        else{
            System.out.println(false);
        }
    }

    /**
     * @param files
     * java文件实时编译demo，以后可以据此修改方法
     */
    private static void javaCompires(File... files) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(files);

        ArrayList<String> ops = new ArrayList<String>();
        ops.add("-classpath");
        ops.add(CLASS_PATH);

        JavaCompiler.CompilationTask task =compiler.getTask(null, manager, null, ops, null, it);
        task.call();
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
        Object obj = cls.newInstance();

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
