package quan.test.arithmetic;

import quan.test.arithmetic.ast.*;

/**
 * 四则运算语法分析器 <br/>
 * 语法规则如下: <br/>
 * value : integer | "(" expression ")" <br/>
 * factor : [ "-" ] ( value ) <br/>
 * product : factor { ( "*" | "/" ) factor } <br/>
 * expression : product { ( "+" | "-" ) product } <br/>
 */
public class Parser extends Lexer {

    public Parser(String expr) {
        super(expr);
    }

    public Node expression() {
        Node node = product();

        while (isToken('+') || isToken('-')) {
            Token token = removeToken();
            if (token.isType('+')) {
                node = new Addition(node, product());
            } else {
                node = new Subtraction(node, product());
            }
        }

        return node;
    }

    Node product() {
        Node node = factor();

        while (isToken('*') || isToken('/')) {
            Token token = removeToken();
            if (token.isType('*')) {
                node = new Multiplication(node, factor());
            } else {
                node = new Division(node, factor());
            }
        }

        return node;
    }

    Node factor() {
        if (isToken('-')) {
            removeToken();
            return new Negative(value());
        } else {
            return value();
        }
    }

    Node value() {
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
        Parser parser = new Parser("-(1+1)*(5-2)-6/2-1+4*3");
        Node expression = parser.expression();
        System.err.println(expression.calc());
    }

}
