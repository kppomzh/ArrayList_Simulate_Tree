package Exceptions.GrammerMakerError.Impl;

import Exceptions.GrammerMakerError.GrammerBaseException;

public class InfinityRightRecursion extends GrammerBaseException {
    public InfinityRightRecursion(String nodeName){
        super();
        append("非终结符\"");
        append(nodeName);
        append("\"的所有产生式都存在右递归，没有任何产生式能退出循环。");
    }
}
