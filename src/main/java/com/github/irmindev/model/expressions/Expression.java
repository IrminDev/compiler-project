package com.github.irmindev.model.expressions;

public abstract class Expression {
    public <T> T accept(ExpressionVisitor<T> visitor) {
        throw new UnsupportedOperationException("This method should be overridden by subclasses");
    }
}
