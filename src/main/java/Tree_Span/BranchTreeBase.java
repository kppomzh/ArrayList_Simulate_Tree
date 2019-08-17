package Tree_Span;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 生成树
 * 通过语法分析之后的数据存放入基本的生成树当中
 * 执行计划生成的时候依次向下按照深度遍历搜索所有信息
 */
public abstract class BranchTreeBase {
    private String branchName;

    protected ArrayList<BranchTreeBase> Childs;
    protected Iterator<BranchTreeBase> iter;

    protected BranchTreeBase(){
        Childs=new ArrayList<>();
    }

    protected void addChild(BranchTreeBase child){
        Childs.add(child);
    }

    public void AddEnd(){
        iter=Childs.listIterator();
    }

    public BranchTreeBase[] getChilds(){
        return Childs.toArray(new BranchTreeBase[0]);
    }
    public BranchTreeBase getNextChild(){
        return iter.hasNext()?iter.next():null;
    }

    /**
     * @param child
     * @param childType
     * 供所有语法树子树类型自己定义如何处理插入的元素，默认所有的继承类都要调用被保护的addChild
     */
    public void addChild(BranchTreeBase child, String childType){
        addChild(child);
    }
}
