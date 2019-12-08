package GrammarMaker;

import Exceptions.GrammerMakerError.Impl.LexRuleException;
import Utils.GrammerFileReader;

import java.util.*;

public class LexStructureAnalysis {
    private static String numberBase = "INTEGER", textBase = "STRING",endBase="EOF",lowerBase="toLowerCase";
    private Set<String> notAnalysisKeywords, intRely;
    private LinkedList<Map.Entry<String, String>> tempStack;
    private Map<String, Set<Character>> result;
    private Map<String, String> GFRmap;

    private Collection<String> markCollection, keyWordCollection;

    public LexStructureAnalysis() {
        notAnalysisKeywords = new HashSet<>();
        tempStack = new LinkedList<>();
        result = new HashMap<>();
        intRely = new HashSet<>();
        intRely.add(numberBase);
    }

    public int[] AnalysisFunction(Collection<String> markCollection, Collection<String> keyWordCollection) throws LexRuleException {
        this.markCollection = markCollection;
//        this.keyWordCollection = keyWordCollection;

        GFRmap = GrammerFileReader.makeConbinationGrammer(keyWordCollection);
        keywordIngredientResolution(GFRmap);

        return countCharMap();
    }

    /**
     * 初步分解词法文件行中的词法规则
     *
     * @param map
     */
    private void keywordIngredientResolution(Map<String, String> map) {
        //准备
        notAnalysisKeywords = new HashSet<>(map.keySet());

        //无依赖计算，部分有依赖的也能计算出来
        for (Map.Entry<String, String> impl : map.entrySet()) {
            result.put(impl.getKey(), new HashSet<>());
            if (countCollectionForKeyword(impl.getKey(), impl.getValue())) {
                tempStack.add(impl);
            } else {
                notAnalysisKeywords.remove(impl.getKey());
            }
        }

        //轮询计算含依赖的项，递归展递推的一种笨办法
        while (!tempStack.isEmpty()) {
            Map.Entry<String, String> impl = tempStack.removeFirst();
            if (countCollectionForKeyword(impl.getKey(), impl.getValue())) {
                tempStack.addLast(impl);
            } else {
                notAnalysisKeywords.remove(impl.getKey());
            }
        }
    }

    /**
     * 预先对各种字符的状态进行定义
     * 1.关键字和变量名
     * 2.数字
     * 3.字符串的定义符号
     * 4.分隔符号
     * 8.转义符号
     * 9.用于指示识别标识符的时候是否区分大小写，固定名称为toLowerCase
     * -3.注释
     * -2.终止
     * -1.报错
     * 在其他情况下，比如扫描到一个符号既出现在一种状态里，又出现在另一种状态里，那么需要另行赋予一个单独的状态编号。
     * <p>
     * 另外，对于字符和名称无关的情况，比如说对象名称、整型数字等等，就只能预先定义写死了。
     * 虽然说符号处在多种词法状态中会被赋予不同的状态编号，但是有几种例外：
     * 1.注释符号总是不会被单独设置状态编号（我想应该没有那个人会脑袋发热想用字母数字下划线来当注释用的吧？）
     * 2.字符串定义符号总是会赋予3的状态编号
     * 3.转义符号总是'\'（虽然各种Linux shell语言在强定义字符串里不会这么用）
     * 4.默认空格、回车、换行、换页是代表分割单词的显性标识，将状态置0
     *
     * @return
     */
    private int[] countCharMap() throws LexRuleException {
        int charStatus = 10;
        int[] charMap = new int[128];
        for (int i = 0; i < 128; i++) {
            charMap[i] = -1;
        }
        Set<Character> setString=result.remove(textBase),setEOF=result.remove(endBase),
        setLowerCase=result.remove(lowerBase);

        for (String mark : this.markCollection) {
            for (char c : mark.toCharArray()) {
                charMap[c] = 4;
            }
        }

        for (String s : intRely) {
            for (Character c : result.remove(s)) {
                if(charMap[c]==2){
                    continue;
                }
                else if (charMap[c] != -1) {
                    charMap[c] = charStatus;
                } else charMap[c] = 2;
            }
        }
        charStatus++;
        for (String mark : result.keySet()) {
            for (char c : result.get(mark)) {
                if(charMap[c]==1){
                    continue;
                }
                else if (charMap[c] != -1) {
                    charMap[c] = charStatus;
                    charStatus++;
                } else {
                    charMap[c] = 1;
                }
            }
        }

        for (Character c : setString) {
            if(charMap[c]!=-1)
                throw new LexRuleException("has used for other states and String definitions",null,c);
            charMap[c] = 3;
        }
        for (Character c : setEOF) {
            if(charMap[c]!=-1)
                throw new LexRuleException("has used for other states and EOF definitions",null,c);
            charMap[c] = -2;
        }
        for (Character c : setLowerCase) {
            if(charMap[c]!=-1)
                throw new LexRuleException("has used for other states and LowerCase definitions",null,c);
            charMap[c] = 9;
        }
        //默认空格、回车、换行、换页是代表分割单词的显性标识
        charMap[32] = 0;
        charMap[12] = 0;
        charMap[13] = 0;
        charMap[10] = 0;
        charMap[92] = 8;
        return charMap;
    }

    private boolean countCollectionForKeyword(String key, String content) {
        boolean canadd = false;
        int splitINdex = content.indexOf(' ');
        if (splitINdex >= 0) {
            String chars[] = content.split(" ");
            for (int i = 0; i < chars.length; i++) {
                if (GFRmap.containsKey(chars[i])) {
                    if (notAnalysisKeywords.contains(chars[i])) {
                        canadd = true;
                    } else {
                        result.get(key).addAll(result.get(chars[i]));
                    }

                    if (numberBase.contains(chars[i])) intRely.add(key);
                } else {
                    result.get(key).add(chars[i].charAt(0));
                }
            }
        }
        return canadd;
    }
}
