package com.github.irmindev.model.expressions;

import com.github.irmindev.model.Token;

public class ExpressionVariable extends Expression {
    private final Token name;

    public ExpressionVariable(Token.Indetifier name) {
        this.name = name;
    }
    public Token getName() {
        return name;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}