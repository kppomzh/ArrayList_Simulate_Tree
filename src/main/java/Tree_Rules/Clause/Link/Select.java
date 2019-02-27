package Tree_Rules.Clause.Link;

import Tree_Rules.Clause.Loop.ListBase_Loop;
import Tree_Rules.Clause.Tree.Select_item;
import Tree_Rules.Rule_LinkList;
import Tree_Rules.Token.mete.variable.Variable;

public class Select extends Rule_LinkList {
    public Select() {
        super("Select");
        this.add_Rule(new Variable("select","select"));
        this.add_Rule(new Select_item());
        this.add_Rule(new ListBase_Loop());
    }
}
