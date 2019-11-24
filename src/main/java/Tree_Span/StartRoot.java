package Tree_Span;

import bean.Word;

import java.util.Collection;

public class StartRoot extends BranchTreeRoot {
    BranchTreeRoot truestart;

    @Override
    public void addChild(BranchTreeRoot child) {
        truestart=child;
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

    public BranchTreeRoot getRoot(){
        BranchTreeRoot res=truestart;
        truestart=null;
        return res;
    }
}
