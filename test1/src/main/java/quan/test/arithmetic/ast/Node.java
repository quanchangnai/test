package quan.test.arithmetic.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象语法树节点
 */
public abstract class Node {

    private List<Node> children = new ArrayList<>();

    public Node getChild(int index) {
        return children.get(index);
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public int calc() {
        return 0;
    }

}
