package GrammarMaker;

import Exceptions.GrammerMakerError.Impl.InfinityRightRecursion;
import Utils.Bool;
import bean.GrammerMaker.LanguageNodeProperty;
import bean.GrammerMaker.nonTerminalMarkInfo;
import bean.Parser.Rule;

import java.util.*;

public class ASTPropertiesMaker {
    //之所以新建一个Set对象的原因是在此过程中需要移除一些被认定为“循环保持符”的非终结符
    private List<String> toMakenonTerminal;
    private Map<String, nonTerminalMarkInfo> ruleMap;
    private Map<String, LanguageNodeProperty> propMap;
    private Collection<String> markCollection;
    private boolean[] rm;

    public ASTPropertiesMaker(Set<String> ruleNameSet, Map<String, nonTerminalMarkInfo> ruleMap, Collection<String> markCollection) {
        toMakenonTerminal = new ArrayList<>(ruleNameSet);
        this.ruleMap = ruleMap;
        propMap = new HashMap<>();
        this.markCollection = markCollection;
        rm = new boolean[toMakenonTerminal.size()];
    }

    /**
     * Recursive...和CountSingal...之间有可能出现各种循环
     * 但是只要Java还是基于引用赋值的机制
     * 即使一开始LanguageNodeProperty是空白对象（不是null，是刚刚new完之后没对内部成员赋值的对象）
     * 也会在后来的过程中逐渐丰满
     */
    public Collection<LanguageNodeProperty> countASTProperties() throws InfinityRightRecursion {
        int countProp;
        for (countProp = 0; countProp < toMakenonTerminal.size(); countProp++) {
            if (!rm[countProp]) RecursiveAnalysisChildProp(toMakenonTerminal.get(countProp));
        }
        return propMap.values();
    }

    private void RecursiveAnalysisChildProp(String node) throws InfinityRightRecursion {
        boolean[] recursion, terminal;
        LanguageNodeProperty thisProp = new LanguageNodeProperty(node);
        propMap.put(node, thisProp);

        nonTerminalMarkInfo ri = ruleMap.get(node);
        int boolSize=ri.isGiveFollow()?ri.getRules().size()-1:ri.getRules().size();
        recursion = new boolean[boolSize];
        terminal = new boolean[boolSize];
        int loop = 0;
        while (ri.hasNext()) {
            boolean temp[] = CountSingalRule(node, ri.getNextRule(), thisProp);
            if(temp!=null){
                recursion[loop] = temp[0];
                terminal[loop] = temp[1];
                loop++;
            }
        }
        this.FormalStructureCheck(ri, thisProp, recursion, terminal);

        rm[toMakenonTerminal.indexOf(node)] = true;
        ri.setEqualTerminal(thisProp.getTerminalStructure());
    }

    /**
     * 这里处理的是每条Rule中的符号的性质
     * 除了常规的直接右递归和终结符之外，还有一点，当一条产生式中出现多个等价于终结符的非终结符时，这些非终结符所引用的终结符可能是同一个
     * 此时需要一种新的结构来表示按照先后顺序压入终结符
     * 如果设置标记，用linkedlist来保存，读取的时候直接通过位置来读取，这样可以解决类似于SQL语法中“表空间 表名 列名”这种完全展开的
     * 表述状态的分析问题
     * 但是当多个类似的产生式出现了交叉的时候，又是另一件难办的事。好在只要首终结符不一样的话也可以想办法区分一二。
     * @param nonTerminal
     * @param rule
     * @param thisProp
     */
    private boolean[] CountSingalRule(String nonTerminal, Rule rule, LanguageNodeProperty thisProp) throws InfinityRightRecursion {
        //[0]表示这条文法中是否有右递归,[1]表示是不是只有一个终结符
        if(rule == Rule.epsilon){
            return null;
        }
        boolean[] res = new boolean[2];

        res[0] = nonTerminal.equals(rule.getLastMark());

        for (int i = 0; i < rule.getRules().size(); i++) {
            String ident=rule.getRules().get(i);
            if (propMap.containsKey(ident)) {
                thisProp.addPropertyNode(propMap.get(ident));
            } else if (ruleMap.containsKey(ident)) {
                RecursiveAnalysisChildProp(ident);
                thisProp.addPropertyNode(propMap.get(ident));
            } else {
                res[1]=rule.length() == 1;
                thisProp.addAttribute(ident);
            }
        }

        return res;
    }

    /**
     * 这里处理每个非终结符中，产生式与产生式、文法符号与文法符号的关系性质
     * @param thisProp
     * @return
     */
    private void FormalStructureCheck(nonTerminalMarkInfo ri, LanguageNodeProperty thisProp, boolean[] recursion, boolean[] terminal) throws InfinityRightRecursion {
        if (Bool.and(terminal)) {
            thisProp.setTerminalStructure();
            System.out.println(thisProp.getNodeName() + "：全部产生式由终结符组成");
            return;
        }

        if (Bool.and(recursion)) {
            //这种情况很有可能就是为了保持某个非终结符的循环
            String toLoopNode = "";
            //natures用来指示一个短的右递归文法产生式的第一个符号是不是等同于一个有意义的终结符（不是诸如冒号逗号这种用来分隔的标点）
            boolean f = true, natures[] = new boolean[ri.getRules().size()], notgoout = true;
            int loop = 0;
            while (ri.hasNext()) {
                String temp;
                Rule rule = ri.getNextRule();
                //预想的有这样几种情况
                if (rule == Rule.epsilon) {
                    loop++;
                    notgoout = false;
                    continue;
                } else if (ruleMap.containsKey(rule.getFirstMark())) {
                    //第一个符号是非终结符，那么就只能由两个非终结符组成产生式右部，因为一个仅仅保持简单循环的产生式不会有复杂结构；即使有
                    //复杂结构也不行，需要用更高层级的抽象符号完成同位替换，总之不允许复杂结构。
                    if (rule.length() == 2) {
                        temp = rule.getFirstMark();
                    } else if (propMap.get(rule.getFirstMark()).getTerminalStructure()&& rule.length() == 3) {
                        //这种情况和“一个终结符+两个非终结符”组成的产生式是类似的，但是不需要考虑第一个符号到底是什么，当成一个属性处理即可
                        temp = rule.getRules().get(1);
                        natures[loop] = true;
                    } else {
                        toLoopNode = null;
                        continue;
                    }
                } else if (ruleMap.containsKey(rule.getRules().get(1)) && rule.length() == 3) {
                    //第一个符号是终结符，那么就只能由一个终结符+两个非终结符组成产生式右部
                    temp = rule.getRules().get(1);
                    if (!markCollection.contains(rule.getFirstMark())) {
                        //第一个符号不是标点符号，那么就比较麻烦，因为单词在这里很可能代表了某种属性
                        natures[loop] = true;
                    } //第一个符号是标点符号，说明只是简单的分隔符
                } else {
                    toLoopNode = null;
                    break;
                }

                if (!temp.equals(toLoopNode)) {
                    if (f) {
                        toLoopNode = temp;
                        f = false;
                    } else {
                        toLoopNode = null;
                        break;
                    }
                }
                loop++;
            }

            if (notgoout) {
                throw new InfinityRightRecursion(thisProp.getNodeName());
            }
            if (toLoopNode != null) {
                thisProp.setToListNodeName(toLoopNode);
                if (propMap.containsKey(toLoopNode)) {
                    propMap.get(toLoopNode).setLoop(true);
                }
                //所以在这里natures中只要有一个为真，那么这个循环节就不能简单地说除了toLoopNode之外不包含其他的有效信息
                thisProp.setLoop(Bool.or(natures));
            }
            else{
                thisProp.setLoop(true);
            }
        } else if (Bool.or(recursion)) {
            //存在循环，但是不一定是为了保持循环
            thisProp.setLoop(true);
        }
    }
}
