package Tree_Span;

import Exceptions.ASTError.ASTBaseException;
import bean.Word;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 生成树
 * 通过语法分析之后的数据存放入基本的生成树当中
 * 执行计划生成的时候依次向下按照深度遍历搜索所有信息
 */
public abstract class BranchTreeRoot implements Serializable {
    protected String branchName;
    protected List<String> Attributes=new LinkedList<>();

    public abstract void addChild(BranchTreeRoot child);

    public abstract Collection<? extends BranchTreeRoot> getChilds();

    public abstract void SetAttribute(String attr, Word o);

    public abstract Word GetAttribute(String attr) throws ASTBaseException;

    public List<String> GetAttributes(){
        return Attributes;
    }

    public String getBranchName() {
        return branchName;
    }

//    public void setBranchName(String branchName) {
//        this.branchName = branchName;
//    }

}
