package com.github.irmindev.model.statements;

import java.util.List;

public class StatementBlock extends Statement {
    private final List<Statement> statements;

    public StatementBlock(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
