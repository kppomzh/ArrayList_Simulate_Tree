package Tree_Span;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 生成树
 * 通过语法分析之后的数据存放入基本的生成树当中
 * 执行计划生成的时候依次向下按照深度遍历搜索所有信息
 */
public abstract class SpanTreeBase {
    private String branchName;

    protected ArrayList<SpanTreeBase> Childs;
    protected Iterator<SpanTreeBase> iter;

    protected SpanTreeBase(){
        Childs=new ArrayList<>();
    }

    protected void addChild(SpanTreeBase child){
        Childs.add(child);
    }

    public void AddEnd(){
        iter=Childs.listIterator();
    }

    public SpanTreeBase[] getChilds(){
        return Childs.toArray(new SpanTreeBase[0]);
    }
    public SpanTreeBase getNextChild(){
        return iter.hasNext()?iter.next():null;
    }

    /**
     * @param child
     * @param childType
     * 供所有语法树子树类型自己定义如何处理插入的元素，默认所有的继承类都要调用被保护的addChild
     */
    public void addChild(SpanTreeBase child,String childType){
        addChild(child);
    }
}
