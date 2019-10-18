package bean.GrammerMaker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LanguageNodeProperty {
    private String nodeName;
    /**
     * 检测右递归，检查该符号本身的尾符号集是否包含其自身。如果是的话此位置置true
     * 如果toListNodeName被赋值的话，那么isLoop几乎肯定是会为真的，但是反过来就不是这样的。
     */
    private boolean isLoop;

    /**
     * 如果该表达式的目的仅仅是启动循环，分析的非终结符是同一个的话，那么toListNodeName应该填写对应的非终结符
     * 比如在函数里传递参数
     */
    private String toListNodeName=null;
    /**
     * 如果一个非终结符的所有产生式展开后都只有一个终结符，那么此处置true
     * 这个非终结符在表达的时候就可以表达成从多个terminalAttribute中挑选一个的属性。
     */
    private boolean isTerminalStructure;

    private Map<String,LanguageNodeProperty> toLoop;
    private Map<String,LanguageNodeProperty> notLoop;
    private Set<String> terminalAttribute;

    public LanguageNodeProperty(String name){
        nodeName=name;
        toLoop=new HashMap<>();
        notLoop=new HashMap<>();
        isLoop=false;
        isTerminalStructure=false;
        terminalAttribute=new HashSet<>();
    }

    public String getNodeName() {
        return nodeName;
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

    public void addAttribute(String attr){
        terminalAttribute.add(attr);
    }

    public void addPropertyNode(LanguageNodeProperty node){
        if(node.getToListNodeName()!=null){
            if(notLoop.containsKey(node.getToListNodeName())){
                toLoop.put(node.getToListNodeName(),notLoop.get(node.getToListNodeName()));
            }
        }
        else if(node.isLoop()){
            toLoop.put(node.getNodeName(),node);
        }
        else
            notLoop.put(node.getNodeName(),node);
    }

    public Map<String, LanguageNodeProperty> getNotLoopPropertyNode() {
        return notLoop;
    }

    public String[] getTerminalAttribute() {
        return terminalAttribute.toArray(new String[0]);
    }

    public Map<String, LanguageNodeProperty> getToLoopPropertyNode() {
        return toLoop;
    }

    public void setTerminalStructure(){
        isTerminalStructure=true;
    }

    public boolean getTerminalStructure(){
        return isTerminalStructure;
    }
}
