package com.github.irmindev.model.expressions;
import com.github.irmindev.model.Token;

public class ExpressionAssign extends Expression{
    final Token name;
    final Expression value;

    ExpressionAssign(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }
}
