package bean.Parser;

import Exceptions.GrammerMakerError.Impl.LeftCommonFactorConflict;
import Exceptions.ParserError.ParserBaseException;
import Utils.Parser.MultiForkSearchTreeSet;
import bean.GrammerMaker.nonTerminalMarkInfo;
import bean.Word;

import java.io.Serializable;
import java.util.*;

/**
 * 预测分析表实体类
 * nonTerminalMap负责将所有非终结符映射到driverTable[][y]上
 * TerminalMap负责将所有终结符映射到driverTable[x][]上
 */
public class PredictiveAnalysisTable implements Serializable {
    private MultiForkSearchTreeSet[][] driverTable;
    private HashMap<String, Integer> nonTerminalMap;
    private HashMap<String, Integer> TerminalMap;

    public PredictiveAnalysisTable(Collection<String> TerminalWords, Collection<String> nonTerminalWords) {
        Collection<String> usableTerminalWords = new HashSet<>(TerminalWords);
        usableTerminalWords.remove("ε");
        driverTable = new MultiForkSearchTreeSet[usableTerminalWords.size() + 1][nonTerminalWords.size()];

        TerminalMap = new HashMap<>();
        nonTerminalMap = new HashMap<>();
        int i;

        Iterator<String> twi = usableTerminalWords.iterator();
        for (i = 0; twi.hasNext(); i++) {
            String terminalName = twi.next();
            TerminalMap.put(terminalName, i);

            for (int loop = 0; loop < nonTerminalWords.size(); loop++) {
                driverTable[i][loop] = new MultiForkSearchTreeSet(terminalName);
            }
        }
        TerminalMap.put("#", i);
        for (int loop = 0; loop < nonTerminalWords.size(); loop++) {
            driverTable[i][loop] = new MultiForkSearchTreeSet("#");
        }
        Iterator<String> ntwi = nonTerminalWords.iterator();
        for (i = 0; ntwi.hasNext(); i++) {
            String terminalName = ntwi.next();
            nonTerminalMap.put(terminalName, i);
        }
    }

    public void setDriverTable(String terminal, String nonTerminal, Rule r, Map<String, nonTerminalMarkInfo> ruleMap) throws LeftCommonFactorConflict {
        if (terminal.equals("ε")) {
            return;
        }
        driverTable[TerminalMap.get(terminal)][nonTerminalMap.get(nonTerminal)].addObject(r, ruleMap);
//        if(driverTable[TerminalMap.get(terminal)][nonTerminalMap.get(nonTerminal)]==null) {
//            driverTable[TerminalMap.get(terminal)][nonTerminalMap.get(nonTerminal)] = r;
//        }
//        else{
//            throw new LeftCommonFactorConflict(terminal,nonTerminal,r,
//                    driverTable[TerminalMap.get(terminal)][nonTerminalMap.get(nonTerminal)]);
//        }
    }

    public Rule getNextRule(String nonTerminal, Word Terminal, List<Word> words) throws ParserBaseException {
        return driverTable[TerminalMap.get(Terminal.getName())][nonTerminalMap.get(nonTerminal)].searchObject(words);
    }

    public boolean inTerminal(String s) {
        return TerminalMap.containsKey(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().getName().equals(this.getClass().getName())) return false;
        PredictiveAnalysisTable p = (PredictiveAnalysisTable) o;

        return p.nonTerminalMap.equals(this.nonTerminalMap) && p.TerminalMap.equals(this.TerminalMap) && Arrays.deepEquals(p.driverTable, this.driverTable);
    }
}
