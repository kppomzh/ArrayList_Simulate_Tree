package Tree_Rules.Clause.Tree;

import Tree_Rules.Clause.Link.TablespaceTableList;
import Tree_Rules.Rule_Tree;

public class Select_item extends Rule_Tree {
    public Select_item() {
        super("Select_item");
        this.add_Child_Rule(new TablespaceTableList());
    }
}
