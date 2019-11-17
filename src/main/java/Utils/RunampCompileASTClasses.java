package Utils;

import Exceptions.GrammerMakerError.GrammerBaseException;
import Exceptions.GrammerMakerError.Impl.CompileException;
import Tree_Span.BranchTreeRoot;
import Tree_Span.S;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class RunampCompileASTClasses implements Serializable {
    private String classpath1;
    private String classpath2;
//    private String basePath;
//    private URLClassLoader loader;

    private RunampCompileASTClasses() throws UnsupportedEncodingException, MalformedURLException {
        classpath1=java.net.URLDecoder.decode(new Tree_Span.S().getClass().getResource("").getFile(),"utf-8");
        classpath1=classpath1.substring(1,classpath1.indexOf("Tree_Span/"));
        classpath2=java.net.URLDecoder.decode(new Tree_Span.S().getClass().getResource("/").getFile(),"utf-8").substring(1);
//        basePath=classpath2.replace("target/test-classes/","src/main/java/Tree_Span/ImplClasses/");
//        loader=ClassLoader.getSystemClassLoader();
//        loader=new URLClassLoader(new URL[]{new URL("file://"+basePath+"Tree_Span/Impl/")},this.getClass().getClassLoader());

    }

    public static RunampCompileASTClasses getInstance(){
        try {
            return new RunampCompileASTClasses();
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void javaCompile(File... files) throws IOException, GrammerBaseException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(files);

        ArrayList<String> ops = new ArrayList<String>();
        ops.add("-classpath");
        StringBuilder classpath=new StringBuilder();
        classpath.append(classpath1);
//        classpath.delete(classpath.indexOf("Tree_Span/"),classpath.length()-1);
        classpath.insert(0,';');
        classpath.insert(0,classpath2);
        ops.add(classpath.toString());
        ops.add("-sourcepath");
        ops.add(classpath2.replace("target/test-classes/","src/main/java/"));
        ops.add("-d");
        ops.add(classpath1);

        JavaCompiler.CompilationTask task =compiler.getTask(null, manager, diagnostics, ops, null, it);

        if(!task.call()) {
            CompileException e=new CompileException();
            for(Diagnostic d:diagnostics.getDiagnostics()){
                if(d.getKind().equals(Diagnostic.Kind.ERROR)){
                    e.addExceptionClass(d);
                }
            }
            throw e;
        }

        manager.close();
    }

    public BranchTreeRoot ClassLoader(String className) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        try {
            return (BranchTreeRoot) Class.forName(className).getConstructor().newInstance();
        }
        catch (NoClassDefFoundError e){
            return new S();
        } catch (NoSuchMethodException e) {
            return new S();
        }
    }

    public Object getBranchRootFiled(BranchTreeRoot node,String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        if(fieldName.startsWith("List")){
            return node.getClass().getMethod(node.getBranchName()).invoke(node);
        }
        else {
            return node.getClass().getField(fieldName).get(
                    node.getClass().getField(fieldName).getType().getEnclosingConstructor().newInstance());
        }
    }
}
