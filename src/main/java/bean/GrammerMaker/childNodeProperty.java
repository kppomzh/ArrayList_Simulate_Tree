package bean.GrammerMaker;

public class childNodeProperty {
    private String nodeName;
    private boolean isTerminal;
    /**
     * 检测右递归，检查该符号本身的尾符号集是否包含其自身。如果是的话此位置置true
     */
    private boolean isLoop;

    /**
     * 如果该表达式的目的仅仅是启动循环，分析的非终结符是同一个的话，那么toListNodeName应该填写对应的非终结符
     * 比如在函数里传递参数
     */
    private String toListNodeName;

    public childNodeProperty(String name){
        nodeName=name;
    }

    public String getNodeName() {
        return nodeName;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public void setTerminal(boolean terminal) {
        isTerminal = terminal;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public String getToListNodeName() {
        return toListNodeName;
    }

    public void setToListNodeName(String toListNodeName) {
        this.toListNodeName = toListNodeName;
    }
}
