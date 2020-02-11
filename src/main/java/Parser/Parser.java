package Parser;

import Exceptions.ASTError.ASTBaseException;
import Exceptions.ParserError.Impl.InputNotEmpty;
import Exceptions.ParserError.Impl.NullGrammerBranch;
import Exceptions.ParserError.Impl.StackNotEmpty;
import Exceptions.ParserError.Impl.parserMakeTreeException;
import Exceptions.ParserError.ParserBaseException;
import Tree_Span.BranchTreeRoot;
import Tree_Span.StartRoot;
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
    private StartRoot ASTRoot;
    private Stack<BranchTreeRoot> nodePop;
    private Stack<Integer> analysisStackFrame;

    public Parser(PredictiveAnalysisTable pat) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.pat = pat;
        analysisStack = new LinkedList<>();
        analysisStack.push("S");
        runAST = RunampCompileASTClasses.getInstance();
        ASTRoot = new StartRoot();
        nodePop = new Stack<>();
        analysisStackFrame = new Stack<>();
    }

    public BranchTreeRoot Controller(LinkedList<Word> words) throws ParserBaseException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BranchTreeRoot nowTree = ASTRoot;
        List<String> nextListRule;
        Word last = words.getLast();

        while (!analysisStack.isEmpty()) {
            if (words.isEmpty()) {
                nextListRule = pat.getNextRule(analysisStack.getFirst(), sharp, words).getRules();
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
                try {
                    if (analysisStack.getFirst().equals(words.getFirst().getName())) {
                        nowTree.addChild(words.getFirst());
                        analysisStack.pop();
                        words.pop();
                    } else {
                        nextListRule = pat.getNextRule(analysisStack.getFirst(), words.getFirst(), words).getRules();

                        String childClass = analysisStack.pop();
                        BranchTreeRoot childNode = runAST.ClassLoader(childClass);
                        nowTree.addChild(childNode);

                        if (nextListRule.get(0).equals("ε")) {
                            //推导出空串直接弹出栈顶节点，不将该节点加入到正在推导节点的方法，在处理“同一非终结符下所有可能节点的首符集交集不为空”的时候可能会有些麻烦

                        } else {
                            //这种情况说明遇到了不能直接转化成一个终结符的非终结符，
                            //所以接下来将会把这个非终结符在这里对应的文法产生式右部的所有符号逆序压栈
                            //
                            analysisStackFrame.push(analysisStack.size());
                            nodePop.push(nowTree);

                            int i = nextListRule.size() - 1;
                            while (i >= 0) {
                                analysisStack.push(nextListRule.get(i));
                                i--;
                            }

                            nowTree = childNode;
                        }
                    }
                }
                catch (ASTBaseException e){
                    throw new parserMakeTreeException(e.getMessage(), words.getFirst());
                }
                if (analysisStack.size() == analysisStackFrame.peek()) {
                    nowTree = nodePop.pop();
                    analysisStackFrame.pop();
                }
            }
        }
        if (!words.isEmpty()) {
            throw new InputNotEmpty(words.getFirst(), words);
        }

        return ASTRoot.getRoot();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().getName().equals(this.getClass().getName())) return false;
        Parser p = (Parser) o;

        return p.pat.equals(this.pat);
    }
}
