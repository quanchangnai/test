package quan.test.arithmetic.ast;

public class IntLiteral extends Node {

    private String value;

    public IntLiteral(String value) {
        this.value = value;
    }

    @Override
    public int calc() {
        return Integer.parseInt(value);
    }

}
