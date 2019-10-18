package Parser;

import Exceptions.ParserError.Impl.InputNotEmpty;
import Exceptions.ParserError.Impl.StackNotEmpty;
import Exceptions.ParserError.ParserBaseException;
import Tree_Span.BranchTreeRoot;
import bean.Parser.PredictiveAnalysisTable;
import bean.Word;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 语法分析总控程序
 */
public class Parser implements Serializable {
    private static final Word sharp;
    static {
        sharp=new Word("#",0,0);
    }
    private PredictiveAnalysisTable pat;
    private LinkedList<String> analysisStack;

    public Parser(PredictiveAnalysisTable pat){
        this.pat=pat;
        analysisStack=new LinkedList<>();
        analysisStack.push("S");
    }

    public BranchTreeRoot Controller(LinkedList<Word> words) throws ParserBaseException {
//        BranchTreeRoot grammerTree=new Tree_Span.BranchTreeRoot.S();
        BranchTreeRoot nowTree;
        List<String> nextListRule;
        Word last=words.getLast();

        while(!analysisStack.isEmpty()){
            if(words.isEmpty()){
                nextListRule = pat.getNextRule(analysisStack.getFirst(), sharp);
                if(nextListRule==null){
                    throw new StackNotEmpty(last,analysisStack);
                }
                else if (nextListRule.get(0).equals("ε")) {
                    analysisStack.pop();
                }else{
                    analysisStack.pop();
                    while(!nextListRule.isEmpty()) {
                        analysisStack.push(nextListRule.remove(nextListRule.size()-1));
                    }
                }
            }
            else if(analysisStack.getFirst().equals(words.getFirst().getName())){
                analysisStack.pop();
                words.pop();
            }
            else {
                nextListRule = pat.getNextRule(analysisStack.getFirst(), words.getFirst());
                if (nextListRule.get(0).equals("ε")) {
                    analysisStack.pop();
                } else{
                    analysisStack.pop();
                    while(!nextListRule.isEmpty()) {
                        analysisStack.push(nextListRule.remove(nextListRule.size()-1));
                    }
                }
            }
        }
        if(!words.isEmpty()){
            throw new InputNotEmpty(words.getFirst(),words);
        }

        //通过analysisStack找到对应的AST类，但是现在这个工作还暂时不能开展，需要等待AST类的自动生成代码确定AST结构之后再进行。
        return null;
    }
}
