package quan.test.arithmetic.ast;

public class UnaryExpr extends Node {

    private String operator;

    public UnaryExpr(int operator, Node right) {
        this.operator = String.valueOf((char) operator);
        addChild(right);
    }

    @Override
    public int calc() {
        int value = getChild(0).calc();
        switch (operator) {
            case "+":
                return value;
            case "-":
                return -value;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return operator + getChild(0);
    }
}
