package quan.test.arithmetic;

import quan.test.arithmetic.ast.*;

/**
 * 四则运算语法分析器 <br/>
 * 语法规则如下: <br/>
 * factor : int | "(" expression ")" <br/>
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

    public Node product() {
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

    public Node factor() {
        if (isToken(Token.INT)) {
            return new IntLiteral(removeToken().getContent());
        }
        if (isToken('(')) {
            removeToken();
            Node node = expression();
            if (isToken(')')) {
                removeToken();
                return node;
            }
        }

        throw new RuntimeException("语法错误:" + removeToken());
    }

    public void error(Token token) {
        throw new RuntimeException("语法错误:" + token);
    }

    public static void main(String[] args) {
        Parser parser = new Parser("(1+1)*(5-2)-6/2-1+4*3");
        Node expression = parser.expression();
        System.err.println(expression.calc());
    }

}
