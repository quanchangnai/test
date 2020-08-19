package quan.test.arithmetic;

import quan.test.arithmetic.ast.BinaryExpr;
import quan.test.arithmetic.ast.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * 算符优先语法分析器
 */
public class Parser2 extends Parser {

    //二元算符优先级
    private Map<Integer, Integer> precedences = new HashMap<>();

    public Parser2(String expr) {
        super(expr);
        precedences.put((int) '+', 1);
        precedences.put((int) '-', 1);
        precedences.put((int) '*', 2);
        precedences.put((int) '/', 2);
    }


    @Override
    protected Node expr() {
        Node left = factor();
        Token next = getToken();

        while (next != null && next.isOperator()) {
            left = reduce(left);
            next = getToken();
        }

        return left;
    }

    protected Node reduce(Node left) {
        Token operator = pollToken();
        Node right = factor();
        Token next = getToken();

        while (next != null && next.isOperator() && precedences.get(operator.getType()) < precedences.get(next.getType())) {
            right = reduce(right);
            next = getToken();
        }

        return new BinaryExpr(left, operator.getType(), right);
    }

}
