package Tree_Rules;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 带循环的语法分支
 */
public class Rule_Loop extends Rule_LinkList {

    public Rule_Loop(String ruleName) {
        super(ruleName);
    }

    @Override
    public rule getRule(String prefix) {
        if(nowRule==lineofrule.size())
            nowRule=0;

        if(lineofrule.get(nowRule).getRule(prefix)!=null){
            rule Rule=lineofrule.get(nowRule);
            Rule.setParent(this);
            nowRule++;
            return Rule;
        }
        return null;
    }

}
