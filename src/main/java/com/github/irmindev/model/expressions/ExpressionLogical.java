package com.github.irmindev.model.expressions;

import com.github.irmindev.model.Token;
public class ExpressionLogical extends Expression{
    final Expression left;
    final Token operator;
    final Expression right;

    public ExpressionLogical(Expression left, Token operator, Expression right) {
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

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

