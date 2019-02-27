package Tree_Rules.Clause.Link;

import Tree_Rules.Rule_LinkList;
import Tree_Rules.Token.mete.variable.Variable;

public class TablespaceTable extends Rule_LinkList {
    public TablespaceTable() {
        super("TablespaceTable");
        this.add_Rule(new Variable("TablespaceName",null));
        this.add_Rule(new Variable("point","\\."));
        this.add_Rule(new Variable("TableName",null));
    }
}
