package Tree_Span;

/**
 * 生成树
 * 通过语法分析之后的数据存放入基本的生成树当中
 * 执行计划生成的时候依次向下按照深度遍历搜索所有信息
 */
public abstract class SpanTreeBase {
    public abstract SpanTreeBase getChild();
}
