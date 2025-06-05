package com.github.irmindev.model.expressions;

import com.github.irmindev.model.Token;

public class ExpressionUnary extends Expression{
     final Token operator;
     final Expression right;

    public ExpressionUnary(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
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
