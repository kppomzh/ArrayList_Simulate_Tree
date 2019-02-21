package Tree_Rules.Token.mete.variable;

import Tree_Rules.Token.mete.Mete;

/**
 * 变量，目前主要用来指在SQL当中出现的表、列、表空间、序列
 */
public class Variable extends Mete {
    /**
     * @param tokenName 指定该Variable：表、列、表空间、序列
     * @param meteName 指定该Variable的名称
     */
    public Variable(String tokenName, String meteName) {
        super(tokenName, meteName);
    }
}
