package Tree_Rules;

public interface rule {
    //每一条文法产生式分支
    public String getClassname();
    public rule getRule(rule r);

    public boolean equals(Object o);
}
