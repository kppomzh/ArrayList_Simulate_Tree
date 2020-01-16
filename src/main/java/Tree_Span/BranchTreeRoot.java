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
    /**
     * 存放了句子中按照某个产生式顺序依次放进来的文法节点，所以调用的时候可以考虑直接用这个链表来组织程序动作。
     */
    protected List<BranchTreeRoot> wordsQueue=new LinkedList<>();

    public abstract void addChild(BranchTreeRoot child);

    public abstract Word GetAttribute(String attr) throws ASTBaseException;

    public List<BranchTreeRoot> getWordsQueue(){
        return wordsQueue;
    }

    public String getBranchName() {
        return branchName;
    }
}
