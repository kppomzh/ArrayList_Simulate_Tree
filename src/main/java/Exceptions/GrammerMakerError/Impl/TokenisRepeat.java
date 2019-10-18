package Exceptions.GrammerMakerError.Impl;

import Exceptions.GrammerMakerError.GrammerBaseException;

public class TokenisRepeat extends GrammerBaseException {
    public TokenisRepeat(){
        super("三个基本语法文件中存在至少同时出现于两个文件中的重复单词~！");
    }
}
