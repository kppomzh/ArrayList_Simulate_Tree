package Utils.Parser;

import Exceptions.ParserError.Impl.NullGrammerBranch;
import Exceptions.ParserError.ParserBaseException;
import bean.Parser.Rule;
import bean.Word;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用于LL(k)分析的多叉搜索树
 * 所以暂时不考虑通用性，设置成“用Word的名称，寻找对应的Rule”
 * 基本的结构是这样的：
 * 1.每个节点中包含一个层级，表示从根结点出发，这个节点是第几层的，同时定义根节点的层级是1。
 * 2.每个节点都有一个布尔变量nochild，用于指示这一层是否有子节点。
 * 3.每个节点都有一个布尔变量apt，用于指示这一层的object是否正好到此结束
 * 3.每个节点包含多个分支，每个分支对应一个文法符号。分支的结构用ArrayList来做，用位移计算哈希散列；如果实在是有冲突，那么重新扩大ArrayList
 * 再进行散列。（或者偷懒直接用HashMap？至少要先自己试一下低级一点方案的能不能工作，不过如果有两个String的hashCode重复的话肯定就不行了。）
 * 组织LL分析表的时候，对于每一条新加入的文法规则，采取如下操作：
 * 1.如果当前节点的nochild值为true，那么直接将这条文法保存在节点下的object中，并将nochild置为false。
 * 2.如果当前节点的nochild值不为true，首先查看apt的值是否为true，如果是false，则需要将object的rule和再次新建节点向下推。
 */
public class MultiForkSearchTreeSet implements Serializable {
    private Entry root;

    public MultiForkSearchTreeSet(String rootName) {
        root = new Entry(0, rootName);
    }

    public Rule searchObject(List<Word> list) throws ParserBaseException {
        return root.getObject(list);
    }

    public void addObject(Rule rule) {
        root.addObject(rule);
    }

    /**
     * 非常浅的复制
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public MultiForkSearchTreeSet clone() throws CloneNotSupportedException {
        MultiForkSearchTreeSet newSet = new MultiForkSearchTreeSet(root.elementName);
        return newSet;
    }

    class Entry implements Serializable {
        int level;
        String elementName;
        Rule object;
        boolean nochild, apt;
        HashMap<String, Entry> forks;

        public Entry(int up, String elementName) {
            level = up + 1;
            nochild = true;
            apt = false;
            forks = new HashMap<>(8);
            this.elementName = elementName;
        }

        public void addObject(Rule toAdd) {
            if (nochild) {
                object = toAdd;
                nochild = false;
                apt = toAdd.length() == level;
                return;
            } else if (!(apt || object == null)) {
                addToChildEntry(object.getRuleIndex(level), object);
                object = null;
            }

            if (toAdd.length() == level) {
                object = toAdd;
                apt = true;
            } else {
                addToChildEntry(toAdd.getRuleIndex(level), toAdd);
            }
        }

        public Rule getObject(List<Word> searchElements) throws ParserBaseException {
            if (searchElements.size() > level && forks.containsKey(searchElements.get(level))) {
                return forks.get(searchElements.get(level)).getObject(searchElements);
            } else if (object != null) {
                return object;
            } else {
                throw new NullGrammerBranch(searchElements.get(level));
            }
        }

        private void addToChildEntry(String entryName, Rule toAdd) {
            if (!forks.containsKey(entryName)) {
                forks.put(entryName, new Entry(level, entryName));
            }

            forks.get(entryName).addObject(toAdd);
        }
    }

}
