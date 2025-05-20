package com.github.irmindev.model.expressions;

public class ExpressionLiteral extends Expression {
    final Object value;

    public ExpressionLiteral(Object value) {
        this.value = value;
    }
}
