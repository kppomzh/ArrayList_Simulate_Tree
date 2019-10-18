package junit;

import Exceptions.GrammerMakerError.Impl.NullKeyException;
import bean.GrammerMaker.HierarchicalTree;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public class HierarchicalTree_Useable {
    @Test
    public void test() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NullKeyException {
        HierarchicalTree<String, HashSet<Object>,Object> tree;
        tree= new HierarchicalTree("",HashSet.class);
        tree.put("abc","");
        if(tree.getMaxLevel()>0){
            System.out.println("true");
        }
        else {
            System.out.println("false");
        }
    }
}
