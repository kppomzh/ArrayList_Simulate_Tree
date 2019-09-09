package bean.GrammerMaker;

public class childNodeProperty {
    private String nodeName;
    private boolean isTerminal;
    /**
     * 检测右递归，检查该符号本身的尾符号集是否包含其自身。如果是的话此位置置true
     */
    private boolean isLoop;
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
