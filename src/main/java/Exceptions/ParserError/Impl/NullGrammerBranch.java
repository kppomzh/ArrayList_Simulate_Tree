package Exceptions.ParserError.Impl;

import Exceptions.ParserError.ParserBaseException;
import bean.Word;

public class NullGrammerBranch extends ParserBaseException {
    public NullGrammerBranch(String nonTerminal,Word word) {
        super(word.getName(), word.getStayline(), word.getStaylist());

        errMessage.append("该单词在此处没有对应的语法");
    }

    public NullGrammerBranch(String word, int line, int list) {
        super(word, line, list);

        errMessage.append("该单词在此处没有对应的语法");
    }
}
