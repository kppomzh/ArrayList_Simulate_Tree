package Exceptions.GrammerMakerError.Impl;

public class FollowDebugException extends Exception {
    public FollowDebugException(String clasz,String node,String line){
        super("本错误指示在扫描文法产生式 计算follow集的时候出现解环冲突的Debug报错。需要检查"+clasz+"类第"+line+"行附近的逻辑。报错节点是 "+node);
    }
}
