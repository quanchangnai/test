package quan.test.arithmetic.ast;

public class BinaryExpr extends Node {

    private String operator;

    public BinaryExpr(Node left, int operator, Node right) {
        addChild(left);
        addChild(right);
        this.operator = String.valueOf((char) operator);
    }

    @Override
    public int calc() {
        int leftValue = getChild(0).calc();
        int rightValue = getChild(1).calc();

        switch (operator) {
            case "+":
                return leftValue + rightValue;
            case "-":
                return leftValue - rightValue;
            case "*":
                return leftValue * rightValue;
            case "/":
                return leftValue / rightValue;
            case "%":
                return leftValue % rightValue;
        }

        return 0;
    }

    @Override
    public String toString() {
        return getChild(0) + operator + getChild(1);
    }

}
