package quan.test.arithmetic;

import quan.test.arithmetic.ast.BinaryExpr;
import quan.test.arithmetic.ast.IntegerLiteral;
import quan.test.arithmetic.ast.Node;
import quan.test.arithmetic.ast.UnaryExpr;

import java.util.HashMap;
import java.util.Map;

/**
 * 算符优先语法分析器
 */
public class Parser2 extends Lexer {

    //二元算符优先级
    private Map<Integer, Integer> precedences = new HashMap<>();

    public Parser2(String expr) {
        super(expr);
        precedences.put((int) '+', 1);
        precedences.put((int) '-', 1);
        precedences.put((int) '*', 2);
        precedences.put((int) '/', 2);
    }

    public Node expr() {
        Node left = factor();
        Token next = getToken();

        while (next != null && next.isOperator()) {
            left = shift(left);
            next = getToken();
        }

        return left;
    }

    protected Node shift(Node left) {
        Token operator = pollToken();
        Node right = factor();
        Token next = getToken();

        while (next != null && next.isOperator() && precedences.get(operator.getType()) < precedences.get(next.getType())) {
            right = shift(right);
            next = getToken();
        }

        return new BinaryExpr(left, operator.getType(), right);
    }

    protected Node factor() {
        if (isToken('-') || isToken('+')) {
            Token operator = pollToken();
            return new UnaryExpr(operator.getType(), unit());
        } else {
            return unit();
        }
    }

    protected Node unit() {
        if (isToken('(')) {
            pollToken();
            Node expr = expr();
            if (isToken(')')) {
                pollToken();
                return expr;
            }
        } else if (isToken(Token.INTEGER)) {
            return new IntegerLiteral(pollToken().getContent());
        }

        throw new RuntimeException("语法错误:" + pollToken());
    }


    public static void main(String[] args) {
        Parser2 parser2 = new Parser2("-(2-1)*(5-2)-6/2-1+-4*+3");
        Node expr = parser2.expr();
        System.err.println(expr.calc());
    }

}
