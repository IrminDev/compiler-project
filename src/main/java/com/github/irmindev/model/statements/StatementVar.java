package com.github.irmindev.model.statements;

import com.github.irmindev.model.expressions.Expression;

import com.github.irmindev.model.Token;

public abstract class StatementVar extends Statement {
    private final Token identifier;
    private final Expression initializer;

    public StatementVar(Token identifier, Expression initializer) {
        this.identifier = identifier;
        this.initializer = initializer;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public Expression getInitializer() {
        return initializer;
    }
}
