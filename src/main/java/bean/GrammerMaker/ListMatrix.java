package bean.GrammerMaker;

import java.util.*;

public class ListMatrix<K> {
    private int size;
    private List<List<Boolean>> matrix;
    private Map<K,Integer> mapping;

    public ListMatrix(){
        matrix = new ArrayList<>();
        mapping = new HashMap<>();
        size=0;
    }

    public void add(K contains){
        mapping.put(contains,size);

        for(List<Boolean> list:matrix){
            list.add(false);
        }
        List<Boolean> newlist = new ArrayList<>();
        for (int i = 0; i < size + 1; i++) {
            newlist.add(false);
        }
        matrix.add(newlist);

        size++;
    }

    public void addConnect(K from,K to){
        Integer intfrom=mapping.get(from),intto=mapping.get(to);
        matrix.get(intfrom).set(intto,true);
    }

    public boolean isConnect(K from,K to){
        return matrix.get(mapping.get(from)).get(mapping.get(to));
    }

    public void countClosures(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    Boolean p1=matrix.get(j).get(i),p2=matrix.get(i).get(k);
                    matrix.get(j).set(k,matrix.get(j).get(k)|p1&p2);
                }
            }
        }
    }

    /**
     * 给出所有能通过单项路径循环回自身的节点名称Set集合
     * @param deduplication 如果该项为true，则会以随机的方式清理同一条环路上的多个节点，只留下一个代表节点
     * @return
     */
    public Set<K> getLoops(boolean deduplication){
        Set<K> res=new HashSet<>();

        for (Map.Entry<K,Integer> entry:mapping.entrySet()) {
            int local = entry.getValue();
            if(matrix.get(local).get(local))
                res.add(entry.getKey());
        }

        if(deduplication) {
            ArrayList<K> temp= new ArrayList<>(res);

            for (int i=0;i<temp.size();i++) {
                K key = temp.get(i);
                Set<Map.Entry<K,Integer>> es=mapping.entrySet();
                List<Boolean> has=matrix.get(mapping.get(key));

                for(Map.Entry<K,Integer> e:es){
                    if(!e.getKey().equals(key)&&has.get(mapping.get(e.getKey()))){
                        temp.remove(e.getKey());
                    }
                }
            }
            res=new HashSet<>(temp);
        }
        return res;
    }
}
