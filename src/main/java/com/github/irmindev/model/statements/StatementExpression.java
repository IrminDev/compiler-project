package com.github.irmindev.model.statements;

import com.github.irmindev.model.expressions.Expression;

public class StatementExpression extends Statement{
    private final Expression expression;

    public StatementExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
