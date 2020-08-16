package quan.test.arithmetic.ast;

public class BinaryExpr extends Node {

    private int operator;

    public BinaryExpr(Node left, int operator, Node right) {
        addChild(left);
        addChild(right);
        this.operator = operator;
    }

    @Override
    public int calc() {
        int leftValue = getChild(0).calc();
        int rightValue = getChild(1).calc();

        switch (operator) {
            case '+':
                return leftValue + rightValue;
            case '-':
                return leftValue - rightValue;
            case '*':
                return leftValue * rightValue;
            case '/':
                return leftValue / rightValue;
        }

        return 0;
    }

}
