package quan.test.arithmetic;

import quan.test.arithmetic.ast.IntegerLiteral;
import quan.test.arithmetic.ast.Node;
import quan.test.arithmetic.ast.UnaryExpr;

/**
 * 语法分析，四则运算语法规则: <br/>
 * expression = product { ( "+" | "-" ) product } <br/>
 * product = factor { ( "*" | "/" ) factor } <br/>
 * factor = [ "+" | "-" ] unit <br/>
 * unit = integer | "(" expression ")" <br/>
 */
public abstract class Parser extends Lexer {

    public Parser(String expr) {
        super(expr);
    }

    public Node parse() {
        Node root = expr();

        Token next = getToken();
        if (next != null) {
            throw error(next);
        }

        return root;
    }

    protected abstract Node expr();

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

        throw error(pollToken());
    }

    public static void main(String[] args) {
        String expr = "-(2-1)*(5-2)-6/2*2-1+-4*+3";

        try {
            System.err.println("parser1:");
            Parser1 parser1 = new Parser1(expr);
            System.err.println(expr + " = " + parser1.parse().calc());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.err.println("parser2:");
            Parser2 parser2 = new Parser2(expr);
            System.err.println(expr + " = " + parser2.parse().calc());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
