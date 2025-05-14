package com.github.irmindev.model.expressions;

import com.github.irmindev.model.Token;

class ExpressionVariable extends Expression {
    final Token name;

    ExpressionVariable(Token name) {
        this.name = name;
    }
}