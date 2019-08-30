package bean.GrammerMaker;

import bean.Parser.Rule;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RuleInfo {
//    private String rulename;

    private Set<String> firstSet,followSet;
    private List<Rule> rules;

    public RuleInfo(){
        firstSet=new HashSet<>();
        followSet=new HashSet<>();
        rules=new LinkedList<>();
    }

    public void addRule(Rule rule){
        rules.add(rule);
    }

    public Set<String> getFollowSet() {
        return followSet;
    }

    public void addFollowSet(Set<String> Set) {
        this.followSet.addAll(Set);
    }

    public Set<String> getFirstSet() {
        return firstSet;
    }

    public void addFirstSet(Set<String> Set) {
        this.firstSet.addAll(Set);
    }

    public boolean hasNext(){
        return !rules.isEmpty();
    }
    public Rule getNextRule(){
        return rules.remove(0);
    }
}
