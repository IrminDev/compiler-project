package com.github.irmindev.model.statements;

import com.github.irmindev.model.expressions.Expression;

public class StatementPrint extends Statement {
    private final Expression expression;

    public StatementPrint(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
