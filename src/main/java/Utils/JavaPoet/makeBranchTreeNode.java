package Utils.JavaPoet;

import Tree_Span.BranchTreeRoot;
import bean.GrammerMaker.childNodeProperty;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class makeBranchTreeNode {
    private JavaFile BranchTreeNode;
    private TypeSpec.Builder ts;
    private String baseDir="src/main/java",packagePath="Tree_Span.Impl",className;
    private Set<String> nonTerminalMethodSet;
    private MethodSpec.Builder[] OverrideFunctions;//继承方法
    private MethodSpec.Builder Constructor;//构造方法
    private List<File> files;

    public makeBranchTreeNode(String NodeName) throws ClassNotFoundException {
        className=NodeName;
        ts=TypeSpec.classBuilder(className);
        ts.superclass(TypeName.get(BranchTreeRoot.class));

        nonTerminalMethodSet=new HashSet<>();
        OverrideFunctions=makeExtendMethodBuilder();
        Constructor=MethodSpec.constructorBuilder();
        files=new ArrayList<>();
    }

    public File buildFile() throws IOException {
        ts.addMethod(Constructor.build());

        for (int i = 0; i < OverrideFunctions.length; i++) {
            ts.addMethod(OverrideFunctions[i].build());
        }

        BranchTreeNode=JavaFile.builder(packagePath,ts.build()).build();
        BranchTreeNode.writeTo(new File(baseDir));

        return new File(baseDir+'/'+packagePath.replaceAll("\\.","/")+className+".java");
//        BranchTreeNode.toJavaFileObject().openOutputStream();
    }



    /**
     * @param nodes
     * 如果是终结符，则作为一个Attribute加入进去，并且留下SetAttribute的方法
     * 这样应该只有显式的右递归才会清晰的记录下来
     * 具体效果需要在运行中体现，目前不太好推断
     */
    public void AnalysisClass(childNodeProperty... nodes) {
        for (int i = 0; i < nodes.length; i++) {
            childNodeProperty np=nodes[i];
            if(np.isTerminal()){
                regAttribute(np.getNodeName());
            }
            else if(np.isLoop()){
                MethodSpec spec=regListMethod(np.getToListNodeName());
                if(spec!=null){
                    ts.addMethod(spec);
                }
            }
            else if(!(np.isTerminal() || np.isLoop())){
                MethodSpec spec=regListMethod(np.getNodeName());
                if(spec!=null){
                    ts.addMethod(spec);
                }
            }
        }
    }

    /**
     * @param nodeName
     * 1.添加对应名称的Boolean型全局变量
     * 2.初始化为false
     * 3.在SetAttribute方法中添加赋值语句
     */
    private void regAttribute(String nodeName){
        ts.addField(Boolean.class,nodeName,Modifier.PUBLIC);

        Constructor.addStatement("$L=false",nodeName);

        OverrideFunctions[2].addStatement("if($L.equals($S){","attr",nodeName);
        OverrideFunctions[2].addStatement("$L=true",nodeName);
        OverrideFunctions[2].addStatement("}");
    }

    /**
     * @param nodeName
     * @return
     * 1.编辑addChild方法，加入一个if判断
     * 2.编辑构造方法，加入一个初始化
     * 3.编辑TypeSpec，加入一个新变量
     * 4.在getChilds中加入对应的addAll代码
     * 5.加入一个get方法
     */
    private MethodSpec regListMethod(String nodeName) {
        if(nonTerminalMethodSet.contains(nodeName)){
            return null;
        }
        ts.addField(ParameterizedTypeName.get(ArrayList.class,BranchTreeRoot.class),"List"+nodeName,Modifier.PRIVATE);
        Constructor.addStatement("List$L=new ArrayList<>()",nodeName);
        OverrideFunctions[0].addStatement("if(child.getBranchName().equals(\"$S\"){\n        $L.add(child);\n    }",nodeName,"List"+nodeName);

        MethodSpec.Builder mb=MethodSpec.methodBuilder(nodeName);
        mb.returns(ParameterizedTypeName.get(List.class,BranchTreeRoot.class));
        mb.addModifiers(Modifier.PUBLIC);
        mb.addStatement("return List$L;",nodeName);

        OverrideFunctions[2].addStatement("childs.addAll(List$L)",nodeName);
        return mb.build();
    }

    /**
     * @return builder的第一位是addChild方法，第二位是getChilds方法，第三位是SetAttribute方法
     * 用数组固定位置加快查找，避免hash的时间损耗；
     * 各种分支的结构繁杂，很难定义通用结构，所以这些方法在这里也不能做到完全成型。
     * 而后继续构造getChilds，这个方法将所有子分支整合成一个List输出。
     * addChild则应该根据不同的类型分开添加到不同的list或者
     */
    public MethodSpec.Builder[] makeExtendMethodBuilder() {
        MethodSpec.Builder[] builder=new MethodSpec.Builder[3];

        builder[0]=MethodSpec.methodBuilder("addChild");
        builder[0].returns(TypeName.VOID);
        builder[0].addAnnotation(Override.class);
        builder[0].addModifiers(Modifier.PUBLIC);
        builder[0].addParameter(TypeName.get(BranchTreeRoot.class),"child");

        builder[1]=MethodSpec.methodBuilder("getChilds");
        builder[1].returns(ParameterizedTypeName.get(Collection.class,BranchTreeRoot.class));
        builder[1].addAnnotation(Override.class);
        builder[1].addModifiers(Modifier.PUBLIC);
        builder[1].addStatement("$T childs=new $T<>();",ParameterizedTypeName.get(Collection.class,BranchTreeRoot.class),ParameterizedTypeName.get(LinkedList.class));
//        builder[1].addStatement("")

        builder[2]=MethodSpec.methodBuilder("SetAttribute");
        builder[2].returns(TypeName.VOID);
        builder[2].addAnnotation(Override.class);
        builder[2].addModifiers(Modifier.PUBLIC);
        builder[2].addParameter(String.class,"attr");
        builder[2].addParameter(String.class,"o");

        return builder;
    }
}
