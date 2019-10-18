package GrammarMaker;

import Exceptions.GrammerMakerError.Impl.FollowDebugException;
import bean.GrammerMaker.ListMatrix;
import bean.GrammerMaker.RuleInfo;
import bean.KVEntryImpl;
import bean.Parser.Rule;

import java.util.*;

public class FollowCollectionMaker {
    /**
     * 记录文法符号的信息，包括产生式右部，first集，follow集
     */
    private Map<String, RuleInfo> ruleMap;
    /**
     * ruleNameSet记录非终结符存在性
     * keyNameSet记录终结符存在性
     */
    private Set<String> ruleNameSet;


    //存储所有first&&ε∈first?follow:null->follow
    private ListMatrix<String> matrix;
    //记录在文法产生式尾部和产生式左部匹配的非终结符
    //存储所有follow->follow
    private Map<String, Set<String>> sameFollowNonTerminal;
    private List<String> nonDependents;
    /**
     * 记录每个文法符号的依赖，也就是说KVEntryImpl中的文法节点会将自己的first或follow集合传递给Map的key代表的文法节点。
     * 规定KVEntryImpl的key记录follow->follow关系，value记录first&&ε∈first?follow:null->follow
     */
    private Map<String, KVEntryImpl<Set<String>, Set<String>>> nodeDependents;
    private Set<String> loop2LoopCount;

    public FollowCollectionMaker(Map<String, RuleInfo> ruleMap, Set<String> ruleNameSet) {
        matrix = new ListMatrix<>();
        nonDependents = new ArrayList<>(ruleNameSet);

        nodeDependents = new HashMap<>();
        for (String s : nonDependents) {
            KVEntryImpl<Set<String>, Set<String>> temp = new KVEntryImpl<>(new HashSet<>(), new HashSet<>());
            nodeDependents.put(s, temp);
            matrix.add(s);
        }
        this.ruleMap = ruleMap;
        this.ruleNameSet = ruleNameSet;
        sameFollowNonTerminal = new HashMap<>();
    }

    /**
     * 计算主要过程
     */
    public Map<String, RuleInfo> countFollowCollection() throws FollowDebugException {
        ScanAllProduction();
        for (int loop = 0; loop < nonDependents.size(); loop++) {
            countSingalLinkNode(nonDependents.get(loop));
        }

        matrix.countClosures();
        Set<String> loopNodesDeputy = matrix.getLoops(true);
        for (String node : loopNodesDeputy) {
            countLoopLinkNode(node);
        }

        return ruleMap;
    }

    /**
     * 扫描所有产生式，建立文法节点follow集合信息传递关系的邻接矩阵
     */
    public void ScanAllProduction() {
        for (Map.Entry<String, RuleInfo> ruleInfoEntry : ruleMap.entrySet()) {
            RuleInfo ri = ruleInfoEntry.getValue();
            for (Rule rule : ri.getRules()) {
                int i = 0;
                List<String> marks = rule.getRules();

                for (; i < marks.size() - 1; i++) {
                    if (ruleNameSet.contains(marks.get(i))) {
                        if (marks.get(i).equals(marks.get(i + 1))) {
                            //不做任何事情，因为两个符号一样
                        } else if (ruleNameSet.contains(marks.get(i + 1))) {
                            matrix.addConnect(marks.get(i + 1), marks.get(i));
                            nonDependents.remove(marks.get(i));
                            addDependents(marks.get(i), marks.get(i + 1), false);
                        } else {
                            ruleMap.get(marks.get(i)).addTerminaltoFollowSet(marks.get(i + 1));
                        }
                    }
                }
                //避免右递归下的无效传输
                if (ruleNameSet.contains(marks.get(i)) && !marks.get(rule.length() - 1).equals(ruleInfoEntry.getKey())) {
                    if (!sameFollowNonTerminal.containsKey(ruleInfoEntry.getKey()))
                        sameFollowNonTerminal.put(ruleInfoEntry.getKey(), new HashSet<>());
                    sameFollowNonTerminal.get(ruleInfoEntry.getKey()).add(rule.getRules().get(i));
                    nonDependents.remove(rule.getRules().get(i));
                    addDependents(marks.get(i), ruleInfoEntry.getKey(), true);
                }
            }
        }
    }

    /**
     * 从某个节点开始处理递归链式依赖，不能处理环式依赖
     *
     * @param startNode
     */
    public void countSingalLinkNode(String startNode) {
        //所以只有满足startNode不再依赖别的节点才能继续处理
        if (nonDependents.contains(startNode)) {
            //如果这些节点出现在sameNonTerminal中，那么现在已经可以放心的将这个节点的follow集传递给sameNonTerminal中s对应的所有value
            if (sameFollowNonTerminal.containsKey(startNode)) {
                for (String childNode : sameFollowNonTerminal.get(startNode)) {
                    ruleMap.get(childNode).addFollowSet(ruleMap.get(startNode).getFollowSet());
                    delDependents(childNode, startNode, true);
                    countSingalLinkNode(childNode);
                }
                sameFollowNonTerminal.remove(startNode);
            }

            Set<String> direct = matrix.getStartNodeConnects(startNode, true);
            for (String childNode : direct) {
                addFollowSet(startNode, childNode);

                delDependents(childNode, startNode, false);

                //递归
                countSingalLinkNode(childNode);
            }
        }
    }

    /**
     * 处理环式依赖
     * 目前的办法是从一个代表节点出发，在这个环上循环两遍
     * 更正：应该循环到某个节点的follow集不再增长的时候最后循环一遍再停止
     *
     * @param startNode
     */
    private void countLoopLinkNode(String startNode) throws FollowDebugException {
        Set<String> singalLoopNode = matrix.getOnLoopNodes(startNode);
        loop2LoopCount=new HashSet<>(singalLoopNode);

        //环形依赖节点第一次循环
        RecursiveLoopLinkNode(startNode, startNode, new HashSet<>(singalLoopNode), false);

        //第二次循环
        RecursiveLoopLinkNode(startNode, startNode, new HashSet<>(singalLoopNode), true);

        //最后为环上的每个节点单独的递归向下清理链式依赖
        for (String downTraverse : singalLoopNode) {
            countSingalLinkNode(downTraverse);
        }
    }

    private void RecursiveLoopLinkNode(String startNode, String endNode, Set<String> singalLoopNode, boolean delDependent) throws FollowDebugException {
        String fatherNode = startNode, childNode = null;

        Set<String> onLoopChilds = matrix.getStartNodeConnects(fatherNode, true), nextNodes = new HashSet<>();
        /* 计算有直接依赖的节点和处于环上的节点的交集，理想情况下应该集合中只剩一个节点
         * 但是如果出现有多个节点也不需要慌张，在多个节点的中寻找在环上只依赖当前fatherNode的节点去遍历即可。
         * */
        onLoopChilds.retainAll(singalLoopNode);
        Iterator<String> childsIterable = onLoopChilds.iterator();
        while (childsIterable.hasNext()) {
            childNode = childsIterable.next();
            ;
            if (delDependent) {
                delDependents(childNode, fatherNode, false);
            }
            else{
                if(addFollowSet(fatherNode, childNode)){
                    loop2LoopCount.remove(childNode);
                }
                else{
                    //实际上这里整体应该用引用计数来处理
                }
            }

            Set<String> tempChildCount = matrix.getEndNodeConnects(childNode, true);
            tempChildCount.retainAll(singalLoopNode);
            //处于环上的节点和到达childNode的节点的交集只有一个，那么肯定是fatherNode，所以此时可以向这个节点递归
            if (tempChildCount.size() == 1) {
                nextNodes.add(childNode);
            }

            if (!tempChildCount.containsAll(nodeDependents.get(childNode).getValue())) {
                throw new FollowDebugException(this.getClass().getName(), childNode, "175");
            }
        }

        for (String nextNode : nextNodes) {
            if (!endNode.equals(nextNode)) {
                RecursiveLoopLinkNode(nextNode, endNode, singalLoopNode, delDependent);
            }
        }
    }

    /**
     * 仅适用于文法产生式右部继承关系的添加
     * @param fatherule
     * @param childrule
     */
    private boolean addFollowSet(String fatherule, String childrule) {
        int follownum=ruleMap.get(childrule).getFollowSet().size();
        ruleMap.get(childrule).addFollowSet(ruleMap.get(fatherule).getFirstSet());

        //s的first集中是否有ε
        if (ruleMap.get(fatherule).isGiveFollow()) {
            //添加follow集到childrule的follow集
            ruleMap.get(childrule).addFollowSet(ruleMap.get(fatherule).getFollowSet());
        }
        return follownum==ruleMap.get(childrule).getFollowSet().size();
    }

    /**
     * 添加继承依赖关系
     * 如果ForwardInheritance为true，说明这是产生式左部传递follow给产生式右部最后一个非终结符
     *
     * @param childNode
     * @param fatherNode
     * @param ForwardInheritance
     */
    private void addDependents(String childNode, String fatherNode, boolean ForwardInheritance) {
        nonDependents.remove(childNode);
        if (ForwardInheritance) {
            nodeDependents.get(childNode).getKey().add(fatherNode);
        } else {
            nodeDependents.get(childNode).getValue().add(fatherNode);
        }
    }

    /**
     * 删除继承依赖关系
     * 如果ForwardInheritance为true，说明这是在处理sameNonTerminal
     *
     * @param childNode
     * @param fatherNode
     * @param ForwardInheritance
     */
    private void delDependents(String childNode, String fatherNode, boolean ForwardInheritance) {
        if (!nonDependents.contains(childNode)) {
            if (ForwardInheritance) {
                nodeDependents.get(childNode).getKey().remove(fatherNode);
            } else {
                nodeDependents.get(childNode).getValue().remove(fatherNode);
            }

            if (nodeDependents.get(childNode).getKey().size() == 0 && nodeDependents.get(childNode).getValue().size() == 0) {
                //因为需要遍历中插入的原因只能将效率高的Set换成List，因此需要这个contains检查
                if (!nonDependents.contains(childNode)) nonDependents.add(childNode);
            }
        }
    }
}
