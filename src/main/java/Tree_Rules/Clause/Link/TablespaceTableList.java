package Tree_Rules.Clause.Link;

import Tree_Rules.Rule_LinkList;
import Tree_Rules.Token.mete.variable.Variable;

public class TablespaceTableList extends Rule_LinkList {
    public TablespaceTableList() {
        super("TablespaceTableList");
        this.add_Rule(new Variable("TablespaceName",null));
        this.add_Rule(new Variable("point","\\."));
        this.add_Rule(new Variable("TableName",null));
        this.add_Rule(new Variable("point","\\."));
        this.add_Rule(new Variable("ListName",null));
    }
}
