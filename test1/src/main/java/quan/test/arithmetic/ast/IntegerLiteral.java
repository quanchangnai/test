package quan.test.arithmetic.ast;

public class IntegerLiteral extends Node {

    private String value;

    public IntegerLiteral(String value) {
        this.value = value;
    }

    @Override
    public int calc() {
        return Integer.parseInt(value);
    }

}
