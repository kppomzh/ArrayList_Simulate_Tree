package bean.GrammerMaker;

import java.util.HashSet;
import java.util.Set;

public class RuleTree {
    private Set<String> firstMarkSet;

    public RuleTree() {
        firstMarkSet=new HashSet<>();
    }

    public Set<String> getFirstSet() {
        return firstMarkSet;
    }

    public void addFirstSet(String first) {
        this.firstMarkSet.add(first);
    }
}
