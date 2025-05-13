package com.github.irmindev.model.statements;

import java.beans.Expression;

public class StatementPrint extends Statement {
    private final Expression expression;

    public StatementPrint(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
