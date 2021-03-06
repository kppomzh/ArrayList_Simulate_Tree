package junit;

import Tree_Span.BranchTreeRoot;
import Tree_Span.StartRoot;
import Utils.JavaPoet.makeBranchTreeNode;
import bean.GrammerMaker.LanguageNodeProperty;
import bean.Word;
import com.squareup.javapoet.*;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class javapoetDemo {
    @Test
    public void poettest() throws ClassNotFoundException, IOException {
        makeJavaFile("Test").writeTo(new File("src/Analysis/java"));
    }

    public JavaFile makeJavaFile(String classPath) throws ClassNotFoundException {
        TypeSpec ts = makeType(classPath);

        JavaFile jf=JavaFile.builder("Tree_Span.Impl",ts).build();
        return jf;
    }
    public TypeSpec makeType(String className) throws ClassNotFoundException {
        TypeSpec.Builder tsb=TypeSpec.classBuilder(className);
        tsb.superclass(TypeName.get(Class.forName("Tree_Span.BranchTreeRoot")));
        tsb.addField(ParameterizedTypeName.get(ArrayList.class,String.class),"list", Modifier.PRIVATE);
        tsb.addMethod(makeMethod("addList"));

        for(MethodSpec.Builder msb:new makeBranchTreeNode(new LanguageNodeProperty("test")).makeExtendMethodBuilder()){
            tsb.addMethod(msb.build());
        }
        return tsb.build();
    }
    public MethodSpec makeMethod(String methodString) throws ClassNotFoundException {
        MethodSpec.Builder mb=MethodSpec.methodBuilder(methodString);
        mb.returns(TypeName.VOID);
        mb.addModifiers(Modifier.PUBLIC);
        mb.addParameter(TypeName.get(Class.forName("java.lang.String")),"str");
        mb.addStatement("this.list.add($L)","str");
        return mb.build();
    }

    @Test
    public void afterAddCode() throws IOException {
        String baseDir = "src/Analysis/java";

        TypeSpec ts;
//        TypeSpec.Builder tbs=TypeSpec.classBuilder("abc");
        TypeSpec.Builder tbs=TypeSpec.classBuilder(ClassName.get(StartRoot.class));

        tbs.addField(Word.class,"testWord",Modifier.PUBLIC);
        ts=tbs.build();
        JavaFile.builder("Tree_Span", ts).build().writeTo(new File(baseDir));
    }

    @Test
    public void classtrue(){
        BranchTreeRoot b=new Word("aaa");
        System.out.println(b.getClass().getName());
        System.out.println(b.getClass().getSimpleName());
    }
}
