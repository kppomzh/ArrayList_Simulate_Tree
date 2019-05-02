package Tree_Rules;

/**
 * 带循环的语法分支
 */
public class Rule_Loop extends Rule_LinkList {
    public Rule_Loop(String ruleName) {
        super(ruleName);
    }

    /**
     * @param prefix 前缀名称
     * @return
     * <p>如果仅考虑循环的话，什么时候停止循环确实是问题。考虑当循环末尾的时候直接考察开头的结构的首符集是否包含该前缀。
     * 但是如果第一个语法结构就是循环结构的话，那么应该顺序的继续向下考察。
     * 无论如何要记住当前考察的位置。
     * 考察到全没有的时候就应该退出到parent当中继续推断。</p>
     */
    @Override
    public rule getRule(String prefix) {
        int hasNullpoint=-1;

        if(nowRule==lineofrule.size())
            nowRule=0;

        if(lineofrule.get(nowRule).getRule(prefix)!=null){
            return pgetRule();
        }
        //所有的循环结构都应该是可以跳过的
        else if(lineofrule.get(nowRule) instanceof Rule_Loop){
            //记住当前遇到循环结构的位置
            hasNullpoint=nowRule;
            //首先一直检查到该循环结构的尾部，如果中间出现了任何语法结构支持这个首符的话就直接返回这个结构
            for(nowRule=nowRule+1;nowRule<lineofrule.size();nowRule++){
                if(lineofrule.get(nowRule).getRule(prefix)==null){
                    if(lineofrule.get(nowRule) instanceof Rule_Loop)
                        continue;
                    return null;
                }
                else if(lineofrule.get(nowRule).getRule(prefix)!=null){
                    return pgetRule();
                }
            }
            //如果一直到尾部都是循环的话，那么下一步检查从头部到当菜的位置之间是否有循环||有某个结构能接收这个首符
            for(int loop=0;loop<hasNullpoint;loop++){
                if(lineofrule.get(loop).getRule(prefix)==null){
                    if(lineofrule.get(loop) instanceof Rule_Loop)
                        continue;
                    break;
                }
                else if(lineofrule.get(loop).getRule(prefix)!=null){
                    nowRule=loop;
                    return pgetRule();
                }
            }
            //如果还是无法查出的话，则考虑退出循环
        }
        return parent;
    }
}
