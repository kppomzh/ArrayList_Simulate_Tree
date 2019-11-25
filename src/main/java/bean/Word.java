package bean;

import Tree_Span.BranchTreeRoot;

import java.util.Collection;

public class Word extends BranchTreeRoot {
    private String type;//单词的语法分类
    private String name;//单词的实际名称，如果是常量的话就写常量的类型名称
    private String substance;//单词的实际内容，只对常量、对象名称有效
    private int stayline;//单词所在的行
    private int staylist;//单词所在行的第n个位置
    private boolean isMark;//简单表示这是一个符号而不是单词

    public Word(String name,int line,int list){
        this.name=name;
        stayline=line;
        staylist=list;
    }

    public Word(String type){

    }

    public int getStayline() {
        return stayline;
    }

    public int getStaylist() {
        return staylist;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int Length(){
        if(substance==null)
            return name==null?0:name.length();
        else
            return substance.length();
    }

    @Override
    public void addChild(BranchTreeRoot child) {

    }

    @Override
    public Collection<? extends BranchTreeRoot> getChilds() {
        return null;
    }

    @Override
    public void SetAttribute(String attr, Word o) {

    }

    @Override
    public Word GetAttribute(String attr) {
        return this;
    }

    @Override
    public String getBranchName(){
        return this.name;
    }
}
