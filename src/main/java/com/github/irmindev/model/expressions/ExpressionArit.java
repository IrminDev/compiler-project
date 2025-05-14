package com.github.irmindev.model.expressions;
import com.github.irmindev.model.Token;

public class ExpressionArit extends Expression{
    final Expression left;
    final Token operator;
    final Expression right;

    ExpressionArit(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

}
