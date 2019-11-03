package Utils.JavaPoet;

import Tree_Span.BranchTreeRoot;
import bean.GrammerMaker.LanguageNodeProperty;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class makeBranchTreeNode {
    private JavaFile BranchTreeNode;
    private TypeSpec.Builder ts;
    private String baseDir = "src/main/java", packagePath = "Tree_Span.Impl", className;
    private MethodSpec.Builder[] OverrideFunctions;//继承方法
    private MethodSpec.Builder addChild,getChilds,SetAttribute;
    private MethodSpec.Builder Constructor;//构造方法
    private LanguageNodeProperty thisProp;
    private Set<String> not2Attr,buildFiled;

    public makeBranchTreeNode(LanguageNodeProperty thisProp,Set<String> ruleNameSet) throws ClassNotFoundException {
        className = thisProp.getNodeName();
        ts = TypeSpec.classBuilder(className);
        ts.superclass(TypeName.get(BranchTreeRoot.class));

//        nonTerminalMethodSet = new HashSet<>();
        OverrideFunctions = makeExtendMethodBuilder();
        addChild=OverrideFunctions[0];
        getChilds=OverrideFunctions[1];
        SetAttribute=OverrideFunctions[2];
        Constructor = MethodSpec.constructorBuilder();
        Constructor.addModifiers(Modifier.PUBLIC);
        this.thisProp=thisProp;
        not2Attr=ruleNameSet;
        buildFiled=new HashSet<>();
    }

    public File buildFile() throws IOException {
        getChilds.addStatement("return childs");
        ts.addMethod(Constructor.build());

        for (int i = 0; i < OverrideFunctions.length; i++) {
            ts.addMethod(OverrideFunctions[i].build());
        }

        BranchTreeNode = JavaFile.builder(packagePath, ts.build()).build();
        BranchTreeNode.writeTo(new File(baseDir));

        return new File(baseDir + '/' + packagePath.replaceAll("\\.", "/") + '/' +  className + ".java");
    }

    public void AnalysisChildPropClass() {
        //需要建立列表的项
        Map<String,LanguageNodeProperty> toloop=thisProp.getToLoopPropertyNode();
        //不需要建立列表的项
        Map<String,LanguageNodeProperty> notloop=thisProp.getNotLoopPropertyNode();
        //先处理notloop，因为可能遇到那些保持循环的需要将属性类移动到loop中
        for(Map.Entry<String,LanguageNodeProperty> notloopEntry:notloop.entrySet()){
            if(notloopEntry.getValue().getTerminalStructure()){
                regAttribute(notloopEntry.getValue().getNodeName(),"string",notloopEntry.getValue().getTerminalAttribute());
            }
            else if(not2Attr.contains(notloopEntry.getValue().getNodeName())){
                regNormalMethod(notloopEntry.getValue().getNodeName());
            }
            else{
                regAttribute(notloopEntry.getValue().getNodeName(),"bool");
            }
        }

        for(Map.Entry<String,LanguageNodeProperty> toloopEntry:toloop.entrySet()){
            ts.addMethod(regListMethod(toloopEntry.getKey()));
        }
        AnalysisAttrTerminal();
    }

    private void AnalysisAttrTerminal(){
        for(String attr:thisProp.getTerminalAttribute()){
            regAttribute(attr,"bool");
        }
    }

    /**
     * @param attrbute 1.添加对应名称的Boolean型全局变量
     *                 2.初始化为false
     *                 3.在SetAttribute方法中添加赋值语句
     * @param type 指定处理的属性类型，可以是数字形式、bool形式、字符串形式，以及什么样的扩展形式
     * @param obj 如果不是处理bool形式的属性，那么可能需要在多个可选属性中选择一项进行处理，这时候传递可选属性用。如果没有obj数组的话，将
     *            会用简单的赋值函数处理
     */
    private void regAttribute(String attrbute,String type,String... obj) {
        if(this.buildFiled.contains(attrbute))
            return;
        else{
            this.buildFiled.add(attrbute);
        }

        switch(type){
            case "string":
                ts.addField(String.class, attrbute, Modifier.PUBLIC);
                regAttrSetterMethod(attrbute,obj==null||obj.length==0,obj);
                break;
            case "bool":
                //默认按照Boolean处理，所以bool和default合在一起
            default:
                ts.addField(Boolean.class, attrbute, Modifier.PUBLIC);

                Constructor.addStatement("$L=false", attrbute);

                SetAttribute.beginControlFlow("if($L.equals($S))","attr", attrbute);
                SetAttribute.addStatement("$L=true", attrbute);
                SetAttribute.endControlFlow();
                break;
        }
    }

    private void regAttrSetterMethod(String fieldName,boolean toSwitch,String... obj){
        if(this.buildFiled.contains(fieldName))
            return;
        else{
            this.buildFiled.add(fieldName);
        }

        ts.addField(String.class, fieldName, Modifier.PUBLIC);
        Constructor.addStatement("$L=null", fieldName);

        SetAttribute.beginControlFlow("if($L.equals($S))", "attr", fieldName);
        if(toSwitch){
            SetAttribute.beginControlFlow("switch(o)");
            for(String s:obj){
                SetAttribute.addStatement("case $L:",s);
                SetAttribute.addStatement("$L=$L",fieldName,s);
                SetAttribute.addStatement("break");
            }
            SetAttribute.endControlFlow();
        }
        else {
            SetAttribute.addStatement("$L=o", fieldName);
        }
        SetAttribute.endControlFlow();
    }

    /**
     * 1.编辑addChild方法，加入一个if判断
     * 2.编辑构造方法，加入一个初始化新的BranchTreeRoot类的语句
     * 3.编辑TypeSpec，加入一个新变量
     * 4.在getChilds中加入对应的addAll代码
     * 5.加入一个get方法
     * @param fieldName
     * @return
     */
    private void regNormalMethod(String fieldName){
        if(this.buildFiled.contains(fieldName))
            return;
        else{
            this.buildFiled.add(fieldName);
        }

        ts.addField(BranchTreeRoot.class, "Node" + fieldName, Modifier.PUBLIC);
        addChild.beginControlFlow("if(child.getBranchName().equals($S))",fieldName);
        addChild.addStatement("Node$L=child",fieldName);
        addChild.endControlFlow();

//        MethodSpec.Builder mb = MethodSpec.methodBuilder(fieldName);
//        mb.returns(BranchTreeRoot.class);
//        mb.addModifiers(Modifier.PUBLIC);
//        mb.addStatement("return Node$L", fieldName);

        getChilds.addStatement("childs.add(Node$L)", fieldName);
//        return mb.build();
    }

    /**
     * 1.编辑addChild方法，加入一个if判断
     * 2.编辑构造方法，加入一个初始化新的ArrayList<BranchTreeRoot>类的语句
     * 3.编辑TypeSpec，加入一个新变量
     * 4.在getChilds中加入对应的addAll代码
     * 5.加入一个get方法
     * @param fieldName
     * @return
     */
    private MethodSpec regListMethod(String fieldName) {
        if(this.buildFiled.contains(fieldName))
            return null;
        else{
            this.buildFiled.add(fieldName);
        }

        ts.addField(ParameterizedTypeName.get(ArrayList.class, BranchTreeRoot.class), "List" + fieldName, Modifier.PRIVATE);
        Constructor.addStatement("List$L=new ArrayList<>()", fieldName);
        addChild.beginControlFlow("if(child.getBranchName().equals($S))",fieldName);
        addChild.addStatement("List$L.add(child)",fieldName);
        addChild.endControlFlow();

        MethodSpec.Builder mb = MethodSpec.methodBuilder(fieldName);
        mb.returns(ParameterizedTypeName.get(List.class, BranchTreeRoot.class));
        mb.addModifiers(Modifier.PUBLIC);
        mb.addStatement("return List$L", fieldName);

        getChilds.addStatement("childs.addAll(List$L)", fieldName);
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
        MethodSpec.Builder[] builder = new MethodSpec.Builder[3];

        builder[0] = MethodSpec.methodBuilder("addChild");
        builder[0].returns(TypeName.VOID);
        builder[0].addAnnotation(Override.class);
        builder[0].addModifiers(Modifier.PUBLIC);
        builder[0].addParameter(TypeName.get(BranchTreeRoot.class), "child");

        builder[1] = MethodSpec.methodBuilder("getChilds");
        builder[1].returns(ParameterizedTypeName.get(Collection.class, BranchTreeRoot.class));
        builder[1].addAnnotation(Override.class);
        builder[1].addModifiers(Modifier.PUBLIC);
        builder[1].addStatement("$T childs=new $T<>()", ParameterizedTypeName.get(Collection.class, BranchTreeRoot.class), ParameterizedTypeName.get(LinkedList.class));
//        builder[1].addStatement("")

        builder[2] = MethodSpec.methodBuilder("SetAttribute");
        builder[2].returns(TypeName.VOID);
        builder[2].addAnnotation(Override.class);
        builder[2].addModifiers(Modifier.PUBLIC);
        builder[2].addParameter(String.class, "attr");
        builder[2].addParameter(String.class, "o");

        return builder;
    }
}
