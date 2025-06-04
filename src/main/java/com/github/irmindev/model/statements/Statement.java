package com.github.irmindev.model.statements;

public abstract class Statement {
    public <T> T accept(StatementVisitor<T> visitor) {
        throw new UnsupportedOperationException("This method should be overridden by subclasses");
    }
}
