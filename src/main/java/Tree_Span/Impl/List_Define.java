package Tree_Span.Impl;

import Tree_Span.BranchTreeRoot;

import java.util.Collection;

public class List_Define extends BranchTreeRoot {
    private String Listname,Listtype;
    private Integer[] length;

    public List_Define(){
        length=new Integer[10];
    }
    @Override
    protected void addChild(BranchTreeRoot child) {

    }

    @Override
    protected Collection<? extends BranchTreeRoot> getChilds() {
        return null;
    }

    @Override
    protected void SetAttribute(String attr, String o) {

    }
}
