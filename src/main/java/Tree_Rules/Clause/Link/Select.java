package Tree_Rules.Clause.Link;

import Tree_Rules.Clause.Loop.ListBase_Loop;
import Tree_Rules.Clause.Tree.Select_item;
import Tree_Rules.Rule_LinkList;
import Tree_Rules.Token.mete.variable.Variable;
import Tree_Rules.rule;

import java.util.ArrayList;
import java.util.List;

public class Select extends Rule_LinkList {
    private List<Select_item> ttl;

    public Select() {
        super("Select");
        this.add_Rule(new Variable("select","select"));
        this.add_Rule(new Select_item());
        this.add_Rule(new ListBase_Loop());
        ttl=new ArrayList<>();
    }

    @Override
    public void add_Rule(rule r){
//        switch (r.getClass().getSimpleName()){
//            case Select_item.class.getSimpleName():
//                ttl.add((Select_item) r);
//                break;
//            case ListBase_Loop.class.getSimpleName():
//                ttl.add(((ListBase_Loop)r));
//                break;
//            default:
//        }
    }

//    public List<TablespaceTableList> getItems(){
//        return this.ttl;
//    }
}
