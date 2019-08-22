package bean.Parser;

import bean.Parser.Tree_Rules.Rule_LinkList;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 预测分析表实体类
 * nonTerminalMap负责将所有非终结符映射到driverTable[][y]上
 * TerminalMap负责将所有终结符映射到driverTable[x][]上
 */
public class PredictiveAnalysisTable {
    private Rule_LinkList[][] driverTable;
    private HashMap<String,Integer> nonTerminalMap;
    private HashMap<String,Integer> TerminalMap;

    public PredictiveAnalysisTable(String[] TerminalWords,String[] nonTerminalWords){
        driverTable=new Rule_LinkList[TerminalWords.length][nonTerminalWords.length];
        TerminalMap=new HashMap<>();
        nonTerminalMap=new HashMap<>();

        for (int i = 0; i < TerminalWords.length; i++) {
            TerminalMap.put(TerminalWords[i],i);
        }
        for (int i = 0; i < nonTerminalWords.length; i++) {
            nonTerminalMap.put(nonTerminalWords[i],i);
        }
    }

    public LinkedList<String> getNextRule(String nonTerminal, String Terminal){
        Rule_LinkList toreturn=driverTable[TerminalMap.get(Terminal)][nonTerminalMap.get(nonTerminal)];

        if(toreturn!=null){
            return toreturn.getRules();
        }
        else{
            //throw error
            return null;
        }
    }
}
