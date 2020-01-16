package Utils.JavaPoet;

import Exceptions.ASTError.ASTBaseException;
import Tree_Span.BranchTreeRoot;
import bean.GrammerMaker.LanguageNodeProperty;
import bean.Word;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class makeBranchTreeNode {
    private JavaFile BranchTreeNode;
    private TypeSpec.Builder ts, Start;
    private String baseDir = "src/main/java", packagePath = "Tree_Span.Impl", className;
    private MethodSpec.Builder[] OverrideFunctions;//继承方法
    private MethodSpec.Builder addChild, GetAttribute;
    private MethodSpec.Builder Constructor;//构造方法
    private LanguageNodeProperty thisProp;
    /**
     * not2Attr：非终结符，所以不能建立为属性
     * buildField：记录已经被建立子节点的符号，当然仅限于现在这个节点中
     */
    private Set<String> buildFiled;

    public makeBranchTreeNode(LanguageNodeProperty thisProp) throws ClassNotFoundException {
        className = thisProp.getNodeName();
        ts = TypeSpec.classBuilder(className);
        ts.superclass(TypeName.get(BranchTreeRoot.class));
        ts.addModifiers(Modifier.PUBLIC);

        OverrideFunctions = makeExtendMethodBuilder();
        addChild = OverrideFunctions[0];
        GetAttribute = OverrideFunctions[1];
        Constructor = MethodSpec.constructorBuilder();
        Constructor.addModifiers(Modifier.PUBLIC);
        Constructor.addStatement("branchName=$S",className);
        this.thisProp = thisProp;
        buildFiled = new HashSet<>();
    }

    public File buildFile() throws IOException {
        ts.addMethod(Constructor.build());
        addChild.endControlFlow();
        addChild.addStatement("wordsQueue.add(child)");
        GetAttribute.endControlFlow();
        GetAttribute.addStatement("return res");

        for (int i = 0; i < OverrideFunctions.length; i++) {
            ts.addMethod(OverrideFunctions[i].build());
        }

        BranchTreeNode = JavaFile.builder(packagePath, ts.build()).build();
        BranchTreeNode.writeTo(new File(baseDir));

        return new File(baseDir + '/' + packagePath.replaceAll("\\.", "/") + '/' + className + ".java");
    }

    public void AnalysisChildPropClass() {
        //需要建立列表的项
        Map<String, LanguageNodeProperty> toloop = thisProp.getToLoopPropertyNode();
        //不需要建立列表的项
        Map<String, LanguageNodeProperty> notloop = thisProp.getNotLoopPropertyNode();

        for (Map.Entry<String, LanguageNodeProperty> toloopEntry : toloop.entrySet()) {
            try {
                regListMethod(toloopEntry.getKey());
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }

        for (Map.Entry<String, LanguageNodeProperty> notloopEntry : notloop.entrySet()) {
            if (notloopEntry.getValue().getTerminalStructure()) {
                regAttribute(notloopEntry.getValue().getNodeName(),notloopEntry.getValue().getTerminalAttribute());
            } else {
                regNormalMethod(notloopEntry.getValue().getNodeName());
            }
        }

        AnalysisAttrTerminal();
    }

    private void AnalysisAttrTerminal() {
        for (String attr : thisProp.getTerminalAttribute()) {
            regAttribute(attr,attr);
        }
    }

    /**
     * @param attrbute   1.添加对应名称的Word型全局变量
     *                   2.初始化为null
     *                   3.在SetAttribute方法中添加赋值语句
     * @param //wordname 用于指示出现非终结符等价于终结符的时候的判断条件
     */
    //wordname用于指示出现非终结符等价于终结符的时候的判断条件
    private void regAttribute(String attrbute,String... wordname) {//
        if (this.buildFiled.contains(attrbute)) return;
        else {
            this.buildFiled.add(attrbute);
        }

        try {
            ts.addField(Word.class, attrbute, Modifier.PUBLIC);
        } catch (IllegalArgumentException e) {
            this.buildFiled.remove(attrbute);
            return;
        }
        Constructor.addStatement("$L=null", attrbute);

        for(String word:wordname){
            addChild.addCode("case $S:\n", word);
            GetAttribute.addCode("case $S:\n", word);
        }
        addChild.addStatement("$L=(Word)child", attrbute);
        addChild.addStatement("break");

        GetAttribute.addStatement("res=$L", attrbute);
        GetAttribute.addStatement("break");
    }

    /**
     * 1.编辑addChild方法，加入一个if判断
     * 2.编辑构造方法，加入一个初始化新的BranchTreeRoot类的语句
     * 3.编辑TypeSpec，加入一个新变量
     * 4.在getChilds中加入对应的addAll代码
     * 5.加入一个get方法
     *
     * @param fieldName
     * @return
     */
    private void regNormalMethod(String fieldName) {
        if (this.buildFiled.contains(fieldName)) return;
        else {
            this.buildFiled.add(fieldName);
        }

        ts.addField(BranchTreeRoot.class, "Node" + fieldName, Modifier.PUBLIC);
        addChild.addCode("case $S:\n",fieldName);
        addChild.addStatement("Node$L=child", fieldName);
        addChild.addStatement("break");

        MethodSpec.Builder mb = MethodSpec.methodBuilder("get"+fieldName);
        mb.returns(BranchTreeRoot.class);
        mb.addModifiers(Modifier.PUBLIC);
        mb.addStatement("return Node$L", fieldName);

        ts.addMethod(mb.build());
    }

    /**
     * 1.编辑addChild方法，加入一个if判断
     * 2.编辑构造方法，加入一个初始化新的ArrayList<BranchTreeRoot>类的语句
     * 3.编辑TypeSpec，加入一个新变量
     * 4.在getChilds中加入对应的addAll代码
     * 5.加入一个get方法
     *
     * @param fieldName
     * @return
     */
    private void regListMethod(String fieldName) {
        if (this.buildFiled.contains(fieldName)) return;
        else {
            this.buildFiled.add(fieldName);
        }

        ts.addField(ParameterizedTypeName.get(ArrayList.class, BranchTreeRoot.class), "List" + fieldName, Modifier.PRIVATE);
        Constructor.addStatement("List$L=new ArrayList<>()", fieldName);
        addChild.addCode("case $S:\n",fieldName);
        addChild.addStatement("List$L.add(child)", fieldName);
        addChild.addStatement("break");

        MethodSpec.Builder mb = MethodSpec.methodBuilder("get"+fieldName);
        mb.returns(ParameterizedTypeName.get(List.class, BranchTreeRoot.class));
        mb.addModifiers(Modifier.PUBLIC);
        mb.addStatement("return List$L", fieldName);

        ts.addMethod(mb.build());
    }

    /**
     * @return builder的第一位是addChild方法，第二位是getChilds方法，第三位是SetAttribute方法
     * 用数组固定位置加快查找，避免hash的时间损耗；
     * 各种分支的结构繁杂，很难定义通用结构，所以这些方法在这里也不能做到完全成型。
     * 而后继续构造getChilds，这个方法将所有子分支整合成一个List输出。
     * addChild则应该根据不同的类型分开添加到不同的list或者
     */
    public MethodSpec.Builder[] makeExtendMethodBuilder() {
        MethodSpec.Builder[] builder = new MethodSpec.Builder[2];

        builder[0] = MethodSpec.methodBuilder("addChild");
        builder[0].returns(TypeName.VOID);
        builder[0].addAnnotation(Override.class);
        builder[0].addModifiers(Modifier.PUBLIC);
        builder[0].addParameter(TypeName.get(BranchTreeRoot.class), "child");
        builder[0].addStatement("super.wordsQueue.add(child)");
        builder[0].beginControlFlow("switch(child.getBranchName())");

        builder[1] = MethodSpec.methodBuilder("GetAttribute");
        builder[1].returns(Word.class);
        builder[1].addAnnotation(Override.class);
        builder[1].addModifiers(Modifier.PUBLIC);
        builder[1].addParameter(String.class, "attr");
        builder[1].addException(ASTBaseException.class);
        builder[1].addStatement("Word res=null");
        builder[1].beginControlFlow("switch(attr)");
        return builder;
    }
}
