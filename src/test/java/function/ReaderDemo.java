package function;

import Exceptions.GrammerMakerError.GrammerBaseException;
import Exceptions.GrammerMakerError.Impl.*;
import GrammarMaker.Reader;
import Lex.Lex;
import Parser.Parser;
import Tree_Span.S;
import Utils.RunampCompileASTClasses;
import org.junit.Test;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//import static sun.tools.jconsole.Messages.CLASS_PATH;

public class ReaderDemo {
    @Test
    public void main() throws IOException, GrammerBaseException, GrammerUndefined, ClassNotFoundException, FollowDebugException, IllegalAccessException, InvocationTargetException, InstantiationException {
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

    @Test
    public void JavaCompired() throws IOException {
        Compireing(new File("D:\\Document\\OneDrive\\CodeRepo\\ASL数据库项目\\ArrayList_Simulate_Tree\\src\\main\\java\\Tree_Span\\Impl").listFiles());
    }
    /**
     * @param files
     * java文件实时编译demo，以后可以据此修改方法
     */
    private static void Compireing(File... files) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(files);

        ArrayList<String> ops = new ArrayList<String>();
        ops.add("-classpath");
        StringBuilder classpath=new StringBuilder();
        classpath.append(java.net.URLDecoder.decode(new S().getClass().getResource("").getFile(),"utf-8"));
        classpath.delete(classpath.indexOf("Tree_Span/"),classpath.length()-1);
        classpath.deleteCharAt(0);
        classpath.insert(0,';');
        classpath.insert(0,java.net.URLDecoder.decode(new S().getClass().getResource("/").getFile(),"utf-8"));
        classpath.deleteCharAt(0);
        ops.add(classpath.toString());
        ops.add("-sourcepath");
        ops.add(new S().getClass().getResource("/").getPath().
                replace("target/test-classes/","src/main/java/"));

        JavaCompiler.CompilationTask task =compiler.getTask(null, manager, diagnostics, ops, null, it);

        if(task.call()) {
            System.out.println(compiler.name());
        }
        manager.close();
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
