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
    public Word GetAttribute(String attr) {
        return null;
    }

    public BranchTreeRoot getRoot(){
//        BranchTreeRoot res=truestart;
//        truestart=null;
        return truestart;
    }
}
