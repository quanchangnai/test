package quan.test.arithmetic;

public class Token {

    private int pos;

    private String content;

    private int type;

    public Token(int pos, String content, int type) {
        this.pos = pos;
        this.content = content;
        this.type = type;
    }

    public int getPos() {
        return pos;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public boolean isType(int type) {
        return this.type == type;
    }

    @Override
    public String toString() {
        return pos + ":" + content;
    }

    public static int INTEGER = 200;
}
