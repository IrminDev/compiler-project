package com.github.irmindev.model.expressions;

import com.github.irmindev.model.Token;

public class ExpressionVariable extends Expression {
    final Token name;

    public ExpressionVariable(Token name) {
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