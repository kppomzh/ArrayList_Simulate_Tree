package Tree_Rules.Token;

import Tree_Rules.RuleImpl;
import Tree_Rules.rule;

public class TokenIum extends RuleImpl {
    public TokenIum(String ruleName) {
        super(ruleName);
    }

    @Override
    public String[] getBaseToken(){
        return new String[]{RuleName};
    }

    @Override
    public rule getRule(rule r) {
        if(this.equals(r))
            return this;
        return null;
    }

    @Override
    public rule getRule(String prefix) {
        if(RuleName.equals(prefix))
            return this;
        return null;
    }
}
