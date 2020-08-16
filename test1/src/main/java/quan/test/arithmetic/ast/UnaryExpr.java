package quan.test.arithmetic.ast;

public class UnaryExpr extends Node {

    private int operator;

    public UnaryExpr(int operator, Node right) {
        this.operator = operator;
        addChild(right);
    }

    @Override
    public int calc() {
        int value = getChild(0).calc();
        switch (operator) {
            case '+':
                return value;
            case '-':
                return -value;
            default:
                return 0;
        }
    }

}
