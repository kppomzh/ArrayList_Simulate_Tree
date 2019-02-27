package Tree_Rules.Clause.Loop;

import Tree_Rules.Clause.Tree.Select_item;
import Tree_Rules.Rule_Loop;
import Tree_Rules.Token.mete.variable.Variable;

public class ListBase_Loop extends Rule_Loop {
    public ListBase_Loop() {
        super("ListBase_Loop");
        this.add_Rule(new Variable("comma",","));
        this.add_Rule(new Select_item());
    }
}
