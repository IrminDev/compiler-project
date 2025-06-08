package com.github.irmindev.model.statements;

import com.github.irmindev.model.expressions.Expression;

public class StatementIf extends Statement {
    private final Expression condition;
    private final Statement block;
    private final Statement elseBlock;

    public StatementIf(Expression condition, Statement block, Statement elseBlock) {
        this.condition = condition;
        this.block = block;
        this.elseBlock = elseBlock;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBlock() {
        return block;
    }

    public Statement getElseBlock() {
        return elseBlock;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
