package bean.Parser;

import Exceptions.GrammerMakerError.Impl.LeftCommonFactorConflict;
import Exceptions.ParserError.Impl.NullGrammerBranch;
import Exceptions.ParserError.ParserBaseException;
import bean.Word;

import java.io.Serializable;
import java.util.*;

/**
 * 预测分析表实体类
 * nonTerminalMap负责将所有非终结符映射到driverTable[][y]上
 * TerminalMap负责将所有终结符映射到driverTable[x][]上
 */
public class PredictiveAnalysisTable implements Serializable {
    private Rule[][] driverTable;
    private HashMap<String,Integer> nonTerminalMap;
    private HashMap<String,Integer> TerminalMap;

    public PredictiveAnalysisTable(Collection<String> TerminalWords, Collection<String> nonTerminalWords){
        Collection<String> usableTerminalWords=new HashSet<>(TerminalWords);
        usableTerminalWords.remove("ε");
        driverTable=new Rule[usableTerminalWords.size()+1][nonTerminalWords.size()];
        TerminalMap=new HashMap<>();
        nonTerminalMap=new HashMap<>();
        int i = 0;

        Iterator<String> twi=usableTerminalWords.iterator();
        for (i = 0; twi.hasNext(); i++) {
            TerminalMap.put(twi.next(),i);
        }
        TerminalMap.put("#",i);
        Iterator<String> ntwi=nonTerminalWords.iterator();
        for (i = 0; ntwi.hasNext(); i++) {
            nonTerminalMap.put(ntwi.next(),i);
        }
    }

    public void setDriverTable(String terminal,String nonterminal,Rule r) throws LeftCommonFactorConflict {
        if(terminal.equals("ε")){
            return;
        }
        if(driverTable[TerminalMap.get(terminal)][nonTerminalMap.get(nonterminal)]==null) {
            driverTable[TerminalMap.get(terminal)][nonTerminalMap.get(nonterminal)] = r;
        }
        else{
            throw new LeftCommonFactorConflict(terminal,nonterminal,r,
                    driverTable[TerminalMap.get(terminal)][nonTerminalMap.get(nonterminal)]);
        }
    }

    public List<String> getNextRule(String nonTerminal, Word Terminal) throws ParserBaseException {
        Rule toreturn=driverTable[TerminalMap.get(Terminal.getName())][nonTerminalMap.get(nonTerminal)];

        if(toreturn!=null){
            return toreturn.getRules();
        }
        else{
            throw new NullGrammerBranch(Terminal);
        }
    }

    public boolean inTerminal(String s) {
        return TerminalMap.containsKey(s);
    }
}
