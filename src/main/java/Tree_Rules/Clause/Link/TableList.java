package Tree_Rules.Clause.Link;

import Tree_Rules.Rule_LinkList;
import Tree_Rules.Token.mete.variable.Variable;

public class TableList extends Rule_LinkList {
    public TableList() {
        super("TableList");
        this.add_Rule(new Variable("TableName",null));
        this.add_Rule(new Variable("point","\\."));
        this.add_Rule(new Variable("ListName",null));
    }
}
