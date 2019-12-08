package Lex;

import Exceptions.LexError.Impl.InvalidSymbolException;
import Exceptions.LexError.Impl.SurplusDecimalPointException;
import Exceptions.LexError.Impl.TerminatorNotFoundException;
import Exceptions.LexError.LexBaseException;
import bean.Lex.IdentifierSetter;
import bean.Word;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Before:Word segment
 * 词法分析器
 * 基于有限自动机，状态用函数表示
 */
public class Lex implements Serializable {
    private int nowStatus = 0;
    private String thisSQL;
    private int line = 0, list = 0,nowindex=0;
    private boolean annotationNull=false;
    private IdentifierSetter tokenSet;
    private LexRule lexRule;

    public Lex(IdentifierSetter set,LexRule rule){
        tokenSet=set;
        lexRule=rule;
    }

    public String[] Pretreatment(String allStatmentinOne){
        String[] allStatments=allStatmentinOne.split(";");

        for (int i = 0; i < allStatments.length; i++) {
            allStatments[i]=allStatments[i].strip();
        }

        return allStatments;
    }

    public LinkedList<Word> getWords(String SQL) throws LexBaseException {
        thisSQL = SQL;
        LinkedList<Word> words = new LinkedList<>();
        words.add(new Word(null,0,0));

        upper:
        while(nowindex<thisSQL.length()) {
            switch (nowStatus) {
                case 0://初态
                    status0();
                    break;
                case 1://关键字和变量名
                case 9:
                    words.add(status1());
                    break;
                case 2://数字
                    words.add(status2());
                    break;
                case 3://字符串
                    words.add(status3());
                    break;
                case 4://符号
                case 10:
                    words.add(status4());
                    break;
                case -3://遇到了注释并且在列表里加入了一个null，此时将null移除
                    words.removeLast();
                    nowStatus=0;
                    break;
                case 8:
                    throw new InvalidSymbolException(line,list,'\\');
                case -2:
                    break upper;
                default:
                    throw new InvalidSymbolException(line,list);
            }
        }

        words.removeFirst();
        nowindex=0;
        line = 0;
        list = 0;
        nowStatus=0;
        return words;
    }

    /**
     * 初态不能返回任何单词
     */
    private void status0() throws InvalidSymbolException {
        upper:
        for (int loop = nowindex; loop < thisSQL.length(); loop++) {
            int status = lexRule.getCharStatus(thisSQL.charAt(loop));
            switch (status) {
                case 0:
                    continue;
                case -1:
                    throw new InvalidSymbolException(line, list, thisSQL.charAt(loop));
                case -2:
                default:
                    nowStatus = status;
                    nowindex=loop;
                    break upper;
            }
        }
    }

    //从SQL的index下标开始继续分析
    /**
     * @return 返回由字母下划线和数字组成的单词
     * @throws InvalidSymbolException
     */
    private Word status1() throws LexBaseException {
        StringBuilder sb = new StringBuilder();
        boolean toLowerCase=true;
        int loop = nowindex;
        upper:
        for (; loop < thisSQL.length(); loop++) {
            int status = lexRule.getCharStatus(thisSQL.charAt(loop));
            switch (status) {
                case 1:
                case 2:
                    if(toLowerCase)
                        sb.append(getSmallLetter(thisSQL.charAt(loop)));
                    else
                        sb.append(thisSQL.charAt(loop));
                    break;
                case -1:
                    throw new InvalidSymbolException(line, list, thisSQL.charAt(loop));
                case 9://扫描到"符号
                    toLowerCase=!toLowerCase;
                    break;
                default:
                    nowStatus = status;
                    break upper;
            }
        }
        if(!toLowerCase)
            throw new TerminatorNotFoundException(line,list,'\"');

        nowindex=loop;
        if(tokenSet.isIdentifier(sb.toString())) {
            return new Word(sb.toString(), line, list);
        }
        else{
            Word w=new Word("NormalTAG", line, list);
            w.setSubstance(sb.toString());
            return w;
        }
    }

    /**
     * @return 返回数值，包括整数和小数
     * @throws InvalidSymbolException
     * @throws SurplusDecimalPointException
     */
    private Word status2() throws InvalidSymbolException, SurplusDecimalPointException {
        boolean point = false;

        StringBuilder sb = new StringBuilder();
        int loop = nowindex;
        upper:
        for (; loop < thisSQL.length(); loop++) {
            int status = lexRule.getCharStatus(thisSQL.charAt(loop));
            switch (status) {
                case 2:
                    sb.append(thisSQL.charAt(loop));
                    break;
                case 10:
                    if (point) {
                        throw new SurplusDecimalPointException(line, list);
                    } else {
                        point = true;
                        sb.append(thisSQL.charAt(loop));
                        break;
                    }
//                case 7://扫描到+-符号
//                    nowStatus=4;
//                    break upper;
                case -1:
                    throw new InvalidSymbolException(line, list, thisSQL.charAt(loop));
                default:
                    nowStatus = status;
                    break upper;
            }
        }
        nowindex=loop;
        if(point) {
            Word w=new Word("DOUBLE", line, list);
            w.setSubstance(sb.toString());
            return w;
        }
        else{
            Word w=new Word("INTEGER", line, list);
            w.setSubstance(sb.toString());
            return w;
        }
    }

    /**
     * @return 返回包含于单引号中的字符串值
     * @throws InvalidSymbolException
     */
    private Word status3() throws InvalidSymbolException {
        StringBuilder sb = new StringBuilder();
        int loop = nowindex;
        upper:
        for (; loop < thisSQL.length(); loop++) {
            switch (lexRule.getCharStatus(thisSQL.charAt(loop))) {
                case 8:
                    sb.append(Escape(thisSQL.charAt(loop+1)));
                    loop++;
                    break;
                case 3:
                    break upper;
                default:
                    sb.append(thisSQL.charAt(loop));
                    break;
            }
        }
        Word w=new Word("STRING", line, list);
        w.setSubstance(sb.toString());
        //因为并不知道字符串后面有什么，而字符串识别的自动机在后一个单引号前停下
        //所以要将nowindex加一，并且重置status让程序自己再重置status
        nowindex=loop+1;
        nowStatus=0;
        return w;
    }

    /**
     * @return 返回符号
     * @throws InvalidSymbolException
     * 此时需要到符号表中检索符号是否合法
     */
    private Word status4() throws LexBaseException {
        StringBuilder sb = new StringBuilder();
        int loop = nowindex;
        upper:
        for (; loop < thisSQL.length(); loop++) {
            int status = lexRule.getCharStatus(thisSQL.charAt(loop));
            switch (status) {
                case -1:
                    throw new InvalidSymbolException(line, list, thisSQL.charAt(loop));
                case 4:
                case 10://用于分割标识符的句号
                    //如果组成的符号字符串不在关键字符号表里，那么就会以当前的StringBuilder返回
                    if(tokenSet.isMark(sb.toString()+thisSQL.charAt(loop))) {
                        sb.append(thisSQL.charAt(loop));
                        if(tokenSet.isAnnotation(sb.toString())){
                            nowindex=loop+1;
                            AnnotationMaker(sb);
                            nowStatus=-3;
                            return null;
                        }
                        break;
                    }
                default:
                    nowStatus = status;
                    break upper;
            }
        }
        nowindex=nowindex+sb.length();
        return new Word(sb.toString(),line,list);
    }

    //转义字符处理函数
    private char Escape(char addone) throws InvalidSymbolException
    {
        switch(addone)
        {
            //case 'a': return '\a'; //java不支持\a？
            case 'b': return '\b';
            case 'f': return '\f';
            case 'n': return '\n';
            case 'r': return '\r';
            case 't': return '\t';
            //case 'v': return '\v'; //java不支持\v？
            case '\\': return '\\';
            case '\'': return '\'';
            case '\"': return '\"';
            //case '\?': return '\?';
            default: throw new InvalidSymbolException(line,list,'\\',addone);
        }
    }

    private char getSmallLetter(char c){
        if(c>=65&&c<=90){
            return (char)(c+32);
        }
        else
            return c;
    }

    private void AnnotationMaker(StringBuilder sb) throws TerminatorNotFoundException {
        if(sb.toString().equals("--")){
            while(thisSQL.charAt(nowindex)!='\n'&&nowindex<thisSQL.length()){
                nowindex++;
            }
        }else if(sb.toString().equals("/*")){
            while((thisSQL.charAt(nowindex) != '*' || thisSQL.charAt(nowindex + 1) != '/') &&nowindex<thisSQL.length()-1){
                nowindex++;
            }
            nowindex=nowindex+2;
        }else{
            throw new TerminatorNotFoundException(line,list,sb.toString().toCharArray());
        }
    }

    @Override
    public boolean equals(Object o){
        if(o==null)
            return false;
        if(!o.getClass().getName().equals(this.getClass().getName()))
            return false;
        Lex l=(Lex) o;

        return this.tokenSet.equals(l.tokenSet);
    }
}
