package com.github.irmindev.model.expressions;

public class ExpressionGrouping extends Expression {
    final Expression expression;

    ExpressionGrouping(Expression expression) {
        this.expression = expression;
    }
}
