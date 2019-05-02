package Tree_Rules.Token.mete.variable;

import Tree_Rules.Token.mete.MeteBase;

/**
 * 变量，目前主要用来指在SQL当中出现的表、列、表空间、序列
 */
public class Variable extends MeteBase {
    /**
     * @param tokenName 指定该Variable：表、列、表空间、序列，当然也可以写任何的关键字
     * @param meteName 指定该Variable的名称
     *
     * <p>comment 当这个类用来标识实际关键字的时候，tokenName要写关键字的小写名称，
     *                 meteName当中的内容要和语言中的标记一样，（例如英文句号，tokenName=point,meteName=.）
     *                 当这个类用来标识非实际关键字，例如一个变量名（例如表名）的时候，meteName需要设置null。</p>
     */
    public Variable(String tokenName, String meteName) {
        super(tokenName, meteName);
    }
}
