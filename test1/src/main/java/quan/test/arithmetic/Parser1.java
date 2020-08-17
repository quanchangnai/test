package quan.test.arithmetic;

import quan.test.arithmetic.ast.BinaryExpr;
import quan.test.arithmetic.ast.IntegerLiteral;
import quan.test.arithmetic.ast.Node;
import quan.test.arithmetic.ast.UnaryExpr;

/**
 * LL(1)语法分析器 <br/>
 * 四则运算语法规则: <br/>
 * expression = product { ( "+" | "-" ) product } <br/>
 * product = factor { ( "*" | "/" ) factor } <br/>
 * factor = [ "+" | "-" ] unit <br/>
 * unit = integer | "(" expression ")" <br/>
 */
public class Parser1 extends Lexer {

    public Parser1(String expr) {
        super(expr);
    }

    public Node expr() {
        Node left = product();

        while (isToken('+') || isToken('-')) {
            Token operator = pollToken();
            left = new BinaryExpr(left, operator.getType(), product());
        }

        return left;
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
        if (isToken(Token.INTEGER)) {
            return new IntegerLiteral(pollToken().getContent());
        }

        if (isToken('(')) {
            pollToken();
            Node expr = expr();
            if (isToken(')')) {
                pollToken();
                return expr;
            }
        }

        throw new RuntimeException("语法错误:" + pollToken());
    }

    protected Node product() {
        Node left = factor();

        while (isToken('*') || isToken('/')) {
            Token operator = pollToken();
            left = new BinaryExpr(left, operator.getType(), factor());
        }

        return left;
    }

    public static void main(String[] args) {
        Parser1 parser1 = new Parser1("-(2-1)*(5-2)-6/2-1+4*+3");
        Node expr = parser1.expr();
        System.err.println(expr.calc());
    }
}
