package Tree_Span;

public class BranchTreeRoot extends BranchTreeBase {
    private String SQLMainType;

    public String getSQLType() {
        return SQLMainType;
    }

    public BranchTreeBase getSQLTypeTree() {
        return super.getNextChild();
    }
}
