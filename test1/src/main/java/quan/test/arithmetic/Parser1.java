package quan.test.arithmetic;

import quan.test.arithmetic.ast.BinaryExpr;
import quan.test.arithmetic.ast.Node;

/**
 * LL(1)语法分析
 */
public class Parser1 extends Parser {

    public Parser1(String expr) {
        super(expr);
    }

    @Override
    protected Node expr() {
        Node left = product();

        while (isToken('+') || isToken('-')) {
            Token operator = removeToken();
            left = new BinaryExpr(left, operator.getType(), product());
        }

        return left;
    }

    protected Node product() {
        Node left = factor();

        while (isToken('*') || isToken('/') || isToken('%')) {
            Token operator = removeToken();
            left = new BinaryExpr(left, operator.getType(), factor());
        }

        return left;
    }

}
