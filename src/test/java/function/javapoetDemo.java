package function;

import Utils.JavaPoet.makeBranchTreeNode;
import bean.GrammerMaker.LanguageNodeProperty;
import com.squareup.javapoet.*;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class javapoetDemo {
    @Test
    public void poettest() throws ClassNotFoundException, IOException {
        makeJavaFile("Test").writeTo(new File("src/main/java"));
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
}
