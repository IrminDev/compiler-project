package com.github.irmindev.model.expressions;

class ExpressionLiteral extends Expression {
    final Object value;

    ExpressionLiteral(Object value) {
        this.value = value;
    }
}
