package Lex;

import Exceptions.InvalidSymbolException;
import Exceptions.SurplusDecimalPointException;

import java.util.LinkedList;
import java.util.List;

/**
 * Before:Word segment
 * 词法分析器
 * 基于有限自动机，状态用函数表示
 */
public class Lex {
    private int nowStatus = 0;
    private String thisSQL;
    private int line=0,list=0;

    public List<String> getWords(String SQL) {
        thisSQL = SQL;
        List<String> words = new LinkedList<>();

        switch (nowStatus){
            case -2:
        }

        return words;
    }

    //从SQL的index下标开始继续分析
    private String status1(int index) throws InvalidSymbolException {
        StringBuilder sb = new StringBuilder();
        upper:
        for (int loop = index; loop < thisSQL.length(); loop++) {
            int status = LexRule.getCharStatus(thisSQL.charAt(loop));
            switch (status) {
                case 1:
                case 2:
                    sb.append(thisSQL.charAt(loop));
                    break;
                case -1:
                    throw new InvalidSymbolException(line,list,thisSQL.charAt(loop));
                default:
                    nowStatus = status;
                    break upper;
            }
        }
        return sb.toString();
    }

    private String status2(int index) throws InvalidSymbolException, SurplusDecimalPointException {
        boolean point=false;

        StringBuilder sb = new StringBuilder();
        upper:
        for (int loop = index; loop < thisSQL.length(); loop++) {
            int status = LexRule.getCharStatus(thisSQL.charAt(loop));
            switch (status) {
                case 2:
                    sb.append(thisSQL.charAt(loop));
                    break;
                case 6:
                    if(point){
                        throw new SurplusDecimalPointException(line,list);
                    }
                    else{
                        point=true;
                        sb.append(thisSQL.charAt(loop));
                        break;
                    }
                case -1:
                    throw new InvalidSymbolException(line,list,thisSQL.charAt(loop));
                default:
                    nowStatus = status;
                    break upper;
            }
        }
        return sb.toString();
    }

    private String status3(int index) throws InvalidSymbolException {
        StringBuilder sb = new StringBuilder();
        upper:for (int loop = index; loop < thisSQL.length(); loop++) {
            switch (LexRule.getCharStatus(thisSQL.charAt(loop))) {
                case 3:
                    break upper;
                default:
                    sb.append(thisSQL.charAt(loop));
                    break;
            }
        }
        return sb.toString();
    }

    private String status4(int index) throws InvalidSymbolException {
        StringBuilder sb = new StringBuilder();
        for (int loop = index; loop < thisSQL.length(); loop++) {
            switch (LexRule.getCharStatus(thisSQL.charAt(loop))) {
                case -1:
                    throw new InvalidSymbolException(line,list,thisSQL.charAt(loop));
            }
        }
        return sb.toString();
    }

//    private String statusHardMatch(int index) {
//
//    }
}
