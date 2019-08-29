package Exceptions.GrammerMakerError;

public class TokenisRepeat extends Exception {
    public TokenisRepeat(){
        super("三个基本语法文件中存在至少同时出现于两个文件中的重复单词~！");
    }
}
