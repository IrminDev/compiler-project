package com.github.irmindev.model.statements;

import com.github.irmindev.model.expressions.Expression;

public class StatementLoop extends Statement {
    private final Expression condition;
    private final Statement block;

    public StatementLoop(Expression condition, Statement block) {
        this.condition = condition;
        this.block = block;
    }

    public Statement getBlock() {
        return block;
    }

    public Expression getCondition() {
        return condition;
    }
    
}
