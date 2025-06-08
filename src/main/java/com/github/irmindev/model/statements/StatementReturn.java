package com.github.irmindev.model.statements;

import com.github.irmindev.model.expressions.Expression;

public class StatementReturn extends Statement {
    private final Expression value;

    public StatementReturn(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
