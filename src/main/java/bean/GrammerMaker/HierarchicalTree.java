package bean.GrammerMaker;

import Exceptions.GrammerMakerError.NullKeyException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 层级树
 * 严格来讲，层级树并非典型的树结构，而是有向图；只不过因为有根节点，并且节点间存在明确的单向依赖关系，所以被称为“树”。
 * 层级树是一种k-v模式的数据结构，较为类似HashMap，但是由于所有的value都要通过计算推导得到，所以一开始的所有节点的value都是空的，添加节点的
 * 时候也只是添加节点间关系而已。
 * 层级数作为节点的基本属性，代表从根节点到这个节点的最长连通路径的长度；这个数越小，说明层级越高，越靠近根节点。
 * 层级树的作用是指示节点间的继承关系，继承的是父结点的某些属性。
 * 说继承也并不准确。因为设想的使用场景中，一堆节点的统属关系并不明确，而且最开始除了根节点之外的所有节点的属性都完全不知道，需要：
 * ①梳理节点间的统属关系
 * ②计算每个节点的属性值
 * ③计算每个节点最后从哪些节点继承属性，并加到节点的value值里。所以value的类型一定是Collection的子类。
 * 又因为这个结构叫做“层级”树，所以依照每个节点的统属关系，每个节点都有一个“所属层级”。因为由于节点的定义，一个节点可能继承多个父结点的属性；
 * 这样就导致节点到root节点会有多个不同长度的路径。这样在遍历的时候就没办法简单的用深度或广度优先来达到路径覆盖的遍历。再加上可能出现的环结构，
 * 更加剧了通常意义上遍历的难度。所以必须为每个节点定义所属层级，最后用这个参数进行遍历。
 *
 * 遍历的时候应该从根节点【getRoot()】开始，首先将每个节点的属性继承给子节点，然后从自身的“层级”出发，遍历下一个层级的所有节点
 * 【getLevelNodes(level)】；依此类推。但是如果出现子节点的“层级”低于当前节点的时候，意味着出现了环。此时需要记录节点的名称，并且在未来以此
 * 节点为子树根节点，将其下所有节点再遍历一次。当然当前记录的临时根节点及其下的单继承子节点是不用被再次遍历的。
 *
 * 当遇到一条有向通路上有多个环的情形时，如果两个环没有重叠部分，应该分开计算。如果存在重叠部分，则只需要将两个环层级最低的节点作为子树根节点，
 * 从层级最高的节点开始往下计算即可。（可达性分析与图的同构）
 * 判断环的存在需要用到邻接表adjacencyList，出于节约空间的考虑，邻接表一般只记录节点的子节点，当出现环的征兆的时候再计算子节点。
 *
 * ps.由于懒惰的原因，所以再次从Java数据结构里把HashMap搬出来应付了账。
 * @param <K>
 * @param <V> 是Collection的子类。本类的目的就是存储类似于key的属性信息，所以value必须是Collection的子类。同时这样设计也方便化简一些初
 *           始化操作。
 */
public class HierarchicalTree<K, V extends Collection> {
    private Node<K> root;
    private HashMap<K,V> table;
    private HashMap<K,Node<K>> tree;
    private int mainLevel;
    private List<Set<K>> levelNodes;
    private Class<V> valueType;
    private HashMap<K,Set<K>> adjacencyList;

    public HierarchicalTree(K key,Class<V> valueType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        root = new Node<>(key);
        mainLevel=0;
        levelNodes=new ArrayList<>();
        HashSet<K> rootLevel=new HashSet<>();
        rootLevel.add(key);
        levelNodes.add(rootLevel);
        this.valueType=valueType;
        table=new HashMap<>();
        table.put(key, this.valueType.getConstructor().newInstance());
        tree=new HashMap<>();
        tree.put(key,root);
        adjacencyList=new HashMap<>();
    }

    public List<K> getRoot() {
        return root.getChildren();
    }

    public int getMainLevel() {
        return mainLevel;
    }

    public int size() {
        return table.size();
    }

    public Set<K> getLevelNodes(int level) {
        return levelNodes.get(level);
    }

    public void put(K key,K fatherNodeKey) throws NullKeyException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //严格禁止将root节点作为子节点
        if(key.equals(root.getKey()))
            throw new NullKeyException();
        if(!tree.containsKey(fatherNodeKey))
            throw new NullKeyException();

        int fatherLevel=tree.get(fatherNodeKey).getLevel();
        if(fatherLevel==mainLevel){
            mainLevel++;
            levelNodes.add(new HashSet<>());
        }

        if(table.containsKey(key)){
            int childLevel=tree.get(key).getLevel();
            //此时就需要判断环的存在，更新邻接表
            if(fatherLevel>=childLevel) {
                tree.get(key).setLevel(fatherLevel + 1);
                levelNodes.get(childLevel).remove(key);
                levelNodes.get(fatherLevel + 1).add(key);

                boolean isFind=makeAdjacencyList(key,fatherNodeKey,fatherLevel);
            }
        }
        else {
            Node<K> newNode=new Node<>(key);
            table.put(key, valueType.getConstructor().newInstance());

            newNode.setLevel(fatherLevel+1);
            levelNodes.get(fatherLevel+1).add(key);

            tree.put(key,newNode);
            adjacencyList.put(key,new HashSet<>());
        }
        tree.get(fatherNodeKey).addChildNode(key);
        adjacencyList.get(fatherLevel).add(key);
    }

    public void addValue(K key,V value){
        get(key).addAll(value);
    }

    public V get(K key){
        return table.get(key);
    }

    public List<K> getChildNodes(K fatherKey){
        return tree.get(fatherKey).getChildren();
    }

    private class Node<K> {
        int level;
        final K key;
        List<K> children;

        Node(K key) {
            this.level = 0;
            this.key = key;
            this.children = new ArrayList<>();
        }

        public void setLevel(int level) {
            this.level = level;
        }
        public int getLevel() { return level; }
        public final K getKey()        { return key; }
        public final String toString() { return key.toString(); }

        public final int hashCode() {
            return Objects.hashCode(key);
        }

        public void addChildNode(K node){
            children.add(node);
        }

        public List<K> getChildren() { return children; }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()))
                    return true;
            }
            return false;
        }
    }

    /**
     * @param father
     * @param tofind 如果father的子树序列中存在tofind，返回true
     * @param stopLevel 表示tofind的层级，当遍历完该层级的时候则停止查找
     * @return
     */
    private boolean makeAdjacencyList(K father,K tofind,int stopLevel) {

    }
}
