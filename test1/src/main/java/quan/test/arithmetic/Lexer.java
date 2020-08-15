package quan.test.arithmetic;

import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析器
 */
public class Lexer {

    private List<Token> tokens = new ArrayList<>();

    public Lexer(String expr) {
        StringBuilder integer = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c >= '0' && c <= '9') {
                integer.append(c);
                if (i == expr.length() - 1) {
                    tokens.add(new Token(i, integer.toString(), Token.INT));
                }
            } else {
                if (integer.length() != 0) {
                    tokens.add(new Token(i, integer.toString(), Token.INT));
                    integer = new StringBuilder();
                }
                if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')') {
                    tokens.add(new Token(i, c + "", c));
                } else if (c != ' ') {
                    throw new RuntimeException(String.format("位置%s存在非法字符:%s,", i, c));
                }
            }
        }
    }


    public Token removeToken() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.remove(0);
    }

    public Token getToken() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.get(0);
    }

    public boolean isToken(int type) {
        Token token = getToken();
        return token != null && token.getType() == type;
    }


    public static void main(String[] args) {
        Lexer lexer = new Lexer("1+(11+1)*2");
        for (Token token : lexer.tokens) {
            System.err.println(token);
        }
    }
}
