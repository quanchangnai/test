package quan.test.arithmetic.ast;

/**
 * 括号表达式
 * Created by quanchangnai on 2020/8/20.
 */
public class BracketExpr extends Node {

    public BracketExpr(Node inner) {
        addChild(inner);
    }

    @Override
    public int calc() {
        return getChild(0).calc();
    }

    @Override
    public String toString() {
        return "(" + getChild(0) + ")";
    }
}
