package com.github.irmindev.model.expressions;

public class ExpressionGrouping extends Expression {
    final Expression expression;

    public ExpressionGrouping(Expression expression) {
        this.expression = expression;
    }
    public Expression getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }


}
