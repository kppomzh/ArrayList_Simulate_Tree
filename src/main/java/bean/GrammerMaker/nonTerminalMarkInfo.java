package bean.GrammerMaker;

import bean.Parser.Rule;

import java.util.*;

/**
 * 一个非终结符所应当包含的分析信息
 */
public class nonTerminalMarkInfo {

    private Set<String> firstSet,followSet,selectSet;
    private List<Rule> rules;
    private Iterator<Rule> rulesIterator;
    private boolean giveFollow,equalTerminal;

    public nonTerminalMarkInfo(){
        firstSet=new HashSet<>();
        followSet=new HashSet<>();
        rules=new LinkedList<>();
        giveFollow=false;
        equalTerminal=false;
    }

    public void addRule(Rule rule){
        rules.add(rule);
        if(rule==Rule.epsilon)
            giveFollow=true;
    }

    public Set<String> getFollowSet() {
        return followSet;
    }

    public void addFollowSet(Set<String> Set) {
        this.followSet.addAll(Set);
    }

    public void addTerminaltoFollowSet(String Terminal) {
        this.followSet.add(Terminal);
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

    public Set<String> getSelectSet(){
        return selectSet;
    }

    public void makeSelectSet(){
        if(isGiveFollow()){
            selectSet=new HashSet<>();
            selectSet.addAll(firstSet);
            selectSet.remove("ε");
            selectSet.addAll(followSet);
        }
        else{
            selectSet=firstSet;
        }
    }

    public List<Rule> getRules(){
        return this.rules;
    }

    public boolean hasNext(){
        if(rulesIterator==null)
            rulesIterator=rules.iterator();
        boolean res=rulesIterator.hasNext();
        if(!res){
            //自动重置，下次再来遍历的时候就自动循环回去
            rulesIterator=null;
            System.gc();
        }
        return res;
    }
    public Rule getNextRule(){
        return rulesIterator.next();
    }

    public boolean isGiveFollow() {
        return giveFollow;
    }

//    public boolean isEqualTerminal() {
//        return equalTerminal;
//    }

    public void setEqualTerminal(boolean equalTerminal) {
        this.equalTerminal = equalTerminal;
    }
}
