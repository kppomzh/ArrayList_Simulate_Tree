package Utils;

import Tree_Span.BranchTreeRoot;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RunampCompileASTClasses {
    private RunampCompileASTClasses(){

    }

    public RunampCompileASTClasses getInstance(){
        return new RunampCompileASTClasses();
    }

    private void javaCompile(File... files) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(files);

        String classpath1=java.net.URLDecoder.decode(new BranchTreeRoot.S().getClass().getResource("").getFile(),"utf-8");
        String classpath2=java.net.URLDecoder.decode(new BranchTreeRoot.S().getClass().getResource("/").getFile(),"utf-8");

        ArrayList<String> ops = new ArrayList<String>();
        ops.add("-classpath");
        StringBuilder classpath=new StringBuilder();
        classpath.append(classpath1);
        classpath.delete(classpath.indexOf("Tree_Span/"),classpath.length()-1);
        classpath.deleteCharAt(0);
        classpath.insert(0,';');
        classpath.insert(0,classpath2);
        classpath.deleteCharAt(0);
        ops.add(classpath.toString());
        ops.add("-sourcepath");
        ops.add(classpath2.replace("target/test-classes/","src/main/java/"));
        ops.add("-d");
        ops.add(classpath2.replace("target/test-classes/","src/main/java/Tree_Span/ImplClasses/"));

        JavaCompiler.CompilationTask task =compiler.getTask(null, manager, diagnostics, ops, null, it);

        if(task.call()) {
            System.out.println(compiler.name());
        }
        manager.close();
    }


}
