package Exceptions.GrammerMakerError.Impl;

public class GrammerUndefined extends Exception {
    public GrammerUndefined(String s) {
        super(s+"该文法单位的定义不存在~！");
    }
}
