package bean.GrammerMaker;

import bean.Parser.Rule;

import java.util.*;

public class RuleInfo {
//    private String rulename;

    private Set<String> firstSet,followSet;
    private List<Rule> rules;
    private Iterator<Rule> rulesIterator;

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
    public void addFirstSet(String firstMark) {
        this.firstSet.add(firstMark);
    }

    public List<Rule> getRules(){
        return this.rules;
    }

    public boolean hasNext(){
        if(rulesIterator==null)
            rulesIterator=rules.iterator();
        return rulesIterator.hasNext();
    }
    public Rule getNextRule(){
        return rulesIterator.next();
    }

}
