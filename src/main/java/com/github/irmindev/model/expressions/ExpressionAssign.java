package com.github.irmindev.model.expressions;
import com.github.irmindev.model.Token;

public class ExpressionAssign extends Expression{
    private final Token name;
    private final Expression value;

    public ExpressionAssign(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public Token getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
