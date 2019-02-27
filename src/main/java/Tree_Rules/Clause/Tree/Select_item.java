package Tree_Rules.Clause.Tree;

import Tree_Rules.Clause.Link.TableList;
import Tree_Rules.Clause.Link.TablespaceTableList;
import Tree_Rules.Rule_Tree;
import Tree_Rules.Token.mete.variable.Variable;

public class Select_item extends Rule_Tree {
    public Select_item() {
        super("Select_item");
        this.add_Child_Rule(new Variable("ListName",null));
        this.add_Child_Rule(new TableList());
        this.add_Child_Rule(new TablespaceTableList());
    }
}
