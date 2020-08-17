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

    private Map<Integer, Integer> precedences = new HashMap<>();

    public Parser2(String expr) {
        super(expr);
        precedences.put((int) '+', 1);
        precedences.put((int) '-', 1);
        precedences.put((int) '*', 2);
        precedences.put((int) '/', 2);
    }

    public Node expr() {
        Node right = factor();
        Token next = getToken();

        while (next != null && next.isOperator()) {
            right = shift(right, next.getType());
            next = pollToken();
        }

        return right;
    }

    protected Node shift(Node left, int operator) {
        Node right = factor();
        Token next = getToken();

        while (next != null && next.isOperator() && precedences.get(operator) < precedences.get(next.getType())) {
            pollToken();
            right = shift(right, next.getType());
        }

        return new BinaryExpr(left, operator, right);
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
        Parser2 parser2 = new Parser2("-(2-1)*(5-2)-6/2-1+4*+3");
        Node expr = parser2.expr();
        System.err.println(expr.calc());
    }

}
