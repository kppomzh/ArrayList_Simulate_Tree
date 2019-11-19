package Parser;

import Exceptions.ParserError.Impl.InputNotEmpty;
import Exceptions.ParserError.Impl.StackNotEmpty;
import Exceptions.ParserError.ParserBaseException;
import Tree_Span.BranchTreeRoot;
import Tree_Span.S;
import Utils.RunampCompileASTClasses;
import bean.Parser.PredictiveAnalysisTable;
import bean.Word;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 语法分析总控程序
 */
public class Parser implements Serializable {
    private static final Word sharp;

    static {
        sharp = new Word("#", 0, 0);
    }

    private PredictiveAnalysisTable pat;
    private LinkedList<String> analysisStack;
    //RunampCompileASTClasses类没有办法序列化，所以这里到底怎么引入这个类是个问题，暂时也没想到好的解决方案
    private RunampCompileASTClasses runAST;
    private BranchTreeRoot ASTRoot;
    private Stack<BranchTreeRoot> nodePop;
    private Stack<Integer> analysisStackFrame;

    public Parser(PredictiveAnalysisTable pat) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.pat = pat;
        analysisStack = new LinkedList<>();
        analysisStack.push("S");
        runAST = RunampCompileASTClasses.getInstance();
        ASTRoot = runAST.ClassLoader("Tree_Span.Impl.S");
        nodePop = new Stack<>();
    }

    public BranchTreeRoot Controller(LinkedList<Word> words) throws ParserBaseException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BranchTreeRoot nowTree = ASTRoot;
        List<String> nextListRule;
        Word last = words.getLast();

        while (!analysisStack.isEmpty()) {
            if (words.isEmpty()) {
                nextListRule = pat.getNextRule(analysisStack.getFirst(), sharp);
                if (nextListRule == null) {
                    throw new StackNotEmpty(last, analysisStack);
                } else if (nextListRule.get(0).equals("ε")) {
                    analysisStack.pop();
                } else {
                    analysisStack.pop();
                    while (!nextListRule.isEmpty()) {
                        analysisStack.push(nextListRule.remove(nextListRule.size() - 1));
                    }
                }
            } else {
                if (analysisStack.getFirst().equals(words.getFirst().getName())) {
                    nowTree.SetAttribute(analysisStack.pop(), words.pop());
                } else {
                    nextListRule = pat.getNextRule(analysisStack.getFirst(), words.getFirst());
                    if (nextListRule.size() == 1 && pat.inTerminal(nextListRule.get(0))) {
                        nowTree.SetAttribute(analysisStack.pop(), words.pop());
                    } else {
                        if (nextListRule.get(0).equals("ε")) {
                            analysisStack.pop();
                        } else {
                            BranchTreeRoot childNode = runAST.ClassLoader(analysisStack.getFirst());
                            nowTree.addChild(childNode);
                            nodePop.push(nowTree);
                            analysisStackFrame.push(analysisStack.size());
                            nowTree = childNode;

                            analysisStack.pop();
                            while (!nextListRule.isEmpty()) {
                                analysisStack.push(nextListRule.remove(nextListRule.size() - 1));
                            }
                        }
                    }
                }
                if (analysisStack.size() < analysisStackFrame.peek()) {
                    nowTree = nodePop.pop();
                    analysisStackFrame.pop();
                }
            }
        }
        if (!words.isEmpty()) {
            throw new InputNotEmpty(words.getFirst(), words);
        }

        //通过analysisStack找到对应的AST类，但是现在这个工作还暂时不能开展，需要等待AST类的自动生成代码确定AST结构之后再进行。
        return ASTRoot;
    }

    public void Clear() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ASTRoot = runAST.ClassLoader("Tree_Span.Impl.S");
    }
}
