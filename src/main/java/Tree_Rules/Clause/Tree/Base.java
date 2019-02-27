package Tree_Rules.Clause.Tree;

import Tree_Rules.Clause.Link.Select;
import Tree_Rules.Rule_Tree;

public class Base extends Rule_Tree {
    public Base() {
        super("Base");
        this.add_Child_Rule(new Select());
    }
}
