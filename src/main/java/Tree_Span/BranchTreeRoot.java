package Tree_Span;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 生成树
 * 通过语法分析之后的数据存放入基本的生成树当中
 * 执行计划生成的时候依次向下按照深度遍历搜索所有信息
 */
public abstract class BranchTreeRoot {
    private String branchName;

    protected abstract void addChild(BranchTreeRoot child);

    protected abstract Collection<? extends BranchTreeRoot> getChilds();

    protected abstract void SetAttribute(String attr,String o);

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
