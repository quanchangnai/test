package quan.test.arithmetic.ast;

public class Subtraction extends Node {

    public Subtraction(Node left, Node right) {
        addChild(left);
        addChild(right);
    }

    @Override
    public int calc() {
        return getChild(0).calc() - getChild(1).calc();
    }
}
