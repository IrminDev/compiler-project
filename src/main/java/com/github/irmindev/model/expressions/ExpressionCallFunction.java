package com.github.irmindev.model.expressions;

import java.util.List;

public class ExpressionCallFunction extends Expression{
    final Expression callee;
    // final Token paren;
    final List<Expression> arguments;

    public ExpressionCallFunction(Expression callee, /*Token paren,*/ List<Expression> arguments) {
        this.callee = callee;
        // this.paren = paren;
        this.arguments = arguments;
    }
    public Expression getCallee() {
        return callee;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
