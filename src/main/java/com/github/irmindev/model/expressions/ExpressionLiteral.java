package com.github.irmindev.model.expressions;

public class ExpressionLiteral extends Expression {
    final Object value;

    public ExpressionLiteral(Object value) {
        this.value = value;
    }
    public Object getValue() {
        return value;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
