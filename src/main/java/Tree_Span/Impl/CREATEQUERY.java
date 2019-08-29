package Tree_Span.Impl;

import Tree_Span.BranchTreeRoot;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CREATEQUERY extends BranchTreeRoot {
    private String TABLESPACENAME,TABLENAME;
    private List<List_Define> list_defines;

    public CREATEQUERY(){
        list_defines=new LinkedList<>();
    }

    @Override
    protected void addChild(BranchTreeRoot child) {
        String name = child.getClass().getName();
        if (List_Define.class.getName().equals(name)) {
            list_defines.add(List_Define.class.cast(child));
        } else if (List_Defines.class.getName().equals(name)) {
            list_defines.add(List_Defines.class.cast(child).getList_Define());
        } else {
            throw new IllegalStateException("Unexpected value: " + child.getClass().getName());
        }

    }

    @Override
    protected Collection<List_Define> getChilds() {
        return list_defines;
    }

    @Override
    public void SetAttribute(String attr, String o) {
        switch (attr){
            case "TABLESPACENAME":
                TABLESPACENAME=o;
                break;
            case "TABLENAME":
                TABLENAME=o;
                break;
        }
    }
}
