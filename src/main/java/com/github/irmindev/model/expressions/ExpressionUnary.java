package com.github.irmindev.model.expressions;

import com.github.irmindev.model.Token;

public class ExpressionUnary extends Expression{
    final Token operator;
    final Expression right;

    ExpressionUnary(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }
}
