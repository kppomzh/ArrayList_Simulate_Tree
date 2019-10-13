package bean.GrammerMaker;

import java.util.*;

public class ListMatrix<K> {
    private int size;
    private List<List<Boolean>> matrix, WarshallMatrix;
    private Map<K, Integer> mapping;
    private Map<Integer, K> inverseMapping;

    public ListMatrix() {
        WarshallMatrix = null;
        matrix = new ArrayList<>();
        mapping = new HashMap<>();
        inverseMapping = new HashMap<>();
        size = 0;
    }

    public void add(K contains) {
        mapping.put(contains, size);
        inverseMapping.put(size, contains);

        for (List<Boolean> list : matrix) {
            list.add(false);
        }
        List<Boolean> newlist = new ArrayList<>();
        for (int i = 0; i < size + 1; i++) {
            newlist.add(false);
        }
        matrix.add(newlist);

        size++;
    }

    public void addConnect(K from, K to) {
        Integer intfrom = mapping.get(from), intto = mapping.get(to);
        matrix.get(intfrom).set(intto, true);
    }

    public boolean isConnect(K from, K to) {
        if(mapping.containsKey(from)&&mapping.containsKey(to))
            return matrix.get(mapping.get(from)).get(mapping.get(to));
        else
            return false;
    }

    public Set<K> getStartNodeConnects(K from,boolean directDependence) {
        Set<K> res = new HashSet<>();
        int line = mapping.get(from);
        List<Boolean> toCount = WarshallMatrix == null || directDependence ? matrix.get(line) : WarshallMatrix.get(line);
        for (int loop = 0; loop < size; loop++) {
            if (toCount.get(loop)) res.add(inverseMapping.get(loop));
        }
        return res;
    }

    public Set<K> getEndNodeConnects(K to,boolean directDependence) {
        Set<K> res = new HashSet<>();
        int list = mapping.get(to);
        List<List<Boolean>> toCount = WarshallMatrix == null || directDependence ? matrix : WarshallMatrix;
        for (int loop = 0; loop < size; loop++) {
            if (toCount.get(loop).get(list)) {
                res.add(inverseMapping.get(loop));
            }
        }
        return res;
    }

    public void countClosures() {
        WarshallMatrix = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                WarshallMatrix.add(new ArrayList<>());
                for (int k = 0; k < size; k++) {
                    Boolean p1 = matrix.get(j).get(i), p2 = matrix.get(i).get(k);
                    WarshallMatrix.get(j).add(matrix.get(j).get(k) | p1 & p2);
                }
            }
        }
    }

    /**
     * 给出所有能通过单项路径循环回自身的节点名称Set集合
     * 由于添加文法的顺序基本上还是按照先高后低来添加的，所以大体上更靠近根节点的环还是会先被访问到。
     * 因此需要用LinkedHashSet保持以后遍历时的顺序，避免先计算远离根节点的环。
     * @param deduplication 如果该项为true，则会以随机的方式清理同一条环路上的多个节点，只留下一个代表节点
     * @return
     */
    public Set<K> getLoops(boolean deduplication) {
        Set<K> res = new LinkedHashSet<>();

        for (Map.Entry<K, Integer> entry : mapping.entrySet()) {
            int local = entry.getValue();
            if (WarshallMatrix.get(local).get(local)) res.add(entry.getKey());
        }

        if (deduplication) {
            ArrayList<K> temp = new ArrayList<>(res);

            for (int i = 0; i < temp.size(); i++) {
                K key = temp.get(i);
                temp.removeAll(getOnLoopNodes(key));
                temp.add(i,key);
            }
            res = new LinkedHashSet<>(temp);
        }
        return res;
    }

    /**
     * 只有双向可达的才认为在一个环上，因此如果两个环存在相交的节点，那么就会被认为是一个环。
     * @param Representative
     * @return
     */
    public Set<K> getOnLoopNodes(K Representative){
        Set<K> res=getStartNodeConnects(Representative,false);
        res.retainAll(getEndNodeConnects(Representative, false));
        return res;
    }
}
