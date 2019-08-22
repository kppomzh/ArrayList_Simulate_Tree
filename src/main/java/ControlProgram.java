import TreePropertiesReader.Reader;
import bean.Parser.Tree_Rules.rule;

import java.io.IOException;
import java.util.HashMap;

/**
 * 计划中的总控分析程序
 */
public class ControlProgram {
    private Reader r;
    private HashMap<String, rule> RuleMap;
    //nowRule是指当前正在进行分析的规则
    private rule base,nowRule;
    public ControlProgram() throws IOException {
        r=new Reader();
        r.loadRules("");
        try {
            RuleMap=r.makeMap();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        base=RuleMap.get("Base");
        nowRule=base;
    }

    public boolean StartAnalysis(String[] words){
        for(String word:words) {
            nowRule=nowRule.getRule(word);
        }
        //
        return true;
    }
}
/**
 * 分析中的分析树和最后分析完成产生的语法树应该是不同的结构。
 * 因为分析树考虑的是灵活、尽量能够自由组合所有可能性，但是用于引擎调度数据的语法树则应该尽量的减少冗余代码和数据，
 * 专注于核心的、已知的结构。
 */
