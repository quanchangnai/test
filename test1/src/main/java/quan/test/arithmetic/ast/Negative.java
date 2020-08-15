package quan.test.arithmetic.ast;

public class Negative extends Node {

    public Negative(Node node) {
        addChild(node);
    }

    @Override
    public int calc() {
        return -getChild(0).calc();
    }
}
