package Tree_Rules;

/**
 * 每一条文法产生式分支
 */
public interface rule {
    /**
     * @return 获取规则名称
     */
    String getRuleName();
    rule getRule(rule r);

    /**
     * @param prefix 当前SQL进行到的单词
     * @return 获取规则的子规则，通过规则（单词级别）名称
     */
    rule getRule(String prefix);

    /**
     * @param RuleName 预存的父规则名称
     * @return 获取规则的父规则以继续分析流程
     */
    rule getParent(String RuleName);

    /**
     * @return 获取当前规则的首符集
     */
    String[] getBaseToken();

    boolean equals(Object o);
}
