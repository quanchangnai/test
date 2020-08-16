package quan.test.arithmetic;

import quan.test.arithmetic.ast.*;

/**
 * 语法分析器 <br/>
 * 四则运算语法规则: <br/>
 * unit = integer | "(" expression ")" <br/>
 * factor = [ "+" | "-" ] ( value ) <br/>
 * product = factor { ( "*" | "/" ) factor } <br/>
 * expression = product { ( "+" | "-" ) product } <br/>
 */
public class Parser extends Lexer {

    public Parser(String expr) {
        super(expr);
    }

    public Node expression() {
        Node left = product();

        while (isToken('+') || isToken('-')) {
            Token token = removeToken();
            left = new BinaryExpr(left, token.getType(), product());
        }

        return left;
    }

    Node product() {
        Node left = factor();

        while (isToken('*') || isToken('/')) {
            Token token = removeToken();
            left = new BinaryExpr(left, token.getType(), factor());
        }

        return left;
    }

    Node factor() {
        if (isToken('-') || isToken('+')) {
            Token token = removeToken();
            return new UnaryExpr(token.getType(), unit());
        } else {
            return unit();
        }
    }

    Node unit() {
        if (isToken(Token.INTEGER)) {
            return new IntegerLiteral(removeToken().getContent());
        }

        if (isToken('(')) {
            removeToken();
            Node expression = expression();
            if (isToken(')')) {
                removeToken();
                return expression;
            }
        }

        throw new RuntimeException("语法错误:" + removeToken());
    }

    public static void main(String[] args) {
        Parser parser = new Parser("-(2-1)*(5-2)-6/2-1+4*+3");
        Node expression = parser.expression();
        System.err.println(expression.calc());
    }

}
