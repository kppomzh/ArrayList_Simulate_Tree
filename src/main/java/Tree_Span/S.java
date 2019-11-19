package Tree_Span;

import bean.Word;

import java.util.Collection;

public class S extends BranchTreeRoot {
    public S(){
        branchName="S";
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
        return null;
    }
}
