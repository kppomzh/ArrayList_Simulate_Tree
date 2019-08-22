package Parser;

import Tree_Span.BranchTreeRoot;
import bean.Parser.PredictiveAnalysisTable;
import bean.Word;

import java.util.LinkedList;

/**
 * 语法分析总控程序
 */
public class Parser {
    private PredictiveAnalysisTable pat;
    private LinkedList<String> analysisStack;

    public Parser(){
        analysisStack=new LinkedList<>();
        analysisStack.push("S");
    }

    public BranchTreeRoot Controller(LinkedList<Word> words){
        BranchTreeRoot grammerTree=new BranchTreeRoot();
        LinkedList<String> nextListRule;

        while(!analysisStack.isEmpty()){
            if(words.isEmpty()){
                nextListRule = pat.getNextRule(analysisStack.getFirst(), "#");
                if (nextListRule.get(0).equals("ε")) {
                    analysisStack.pop();
                }else{
                    analysisStack.pop();
                    while(!nextListRule.isEmpty()) {
                        analysisStack.push(nextListRule.removeLast());
                    }
                }
            }
            else if(analysisStack.getFirst().equals(words.getFirst().getName())){
                analysisStack.pop();
                words.pop();
            }
            else {
                nextListRule = pat.getNextRule(analysisStack.getFirst(), words.getFirst().getName());
                if (nextListRule == null) {
                    //throw error
                } else if (nextListRule.get(0).equals("ε")) {
                    analysisStack.pop();
                } else{
                    analysisStack.pop();
                    while(!nextListRule.isEmpty()) {
                        analysisStack.push(nextListRule.removeLast());
                    }
                }
            }
        }
        if(!analysisStack.isEmpty()){

        }
        if(!words.isEmpty()){

        }

        return grammerTree;
    }
}
