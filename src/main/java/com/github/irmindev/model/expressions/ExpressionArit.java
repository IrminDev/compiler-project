package com.github.irmindev.model.expressions;
import com.github.irmindev.model.Token;

public class ExpressionArit extends Expression{
    private final Expression left;
    private final Token operator;
    private final Expression right;

    public ExpressionArit(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    public Expression getLeft() {
        return left;
    }

    public Token getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    // DO this for all the expression classes
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
