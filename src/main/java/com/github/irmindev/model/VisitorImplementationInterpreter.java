package com.github.irmindev.model;

import com.github.irmindev.model.expressions.Expression;
import com.github.irmindev.model.expressions.ExpressionVisitor;
import com.github.irmindev.model.statements.StatementVisitor;

public class VisitorImplementationInterpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {
    private final Environment global;
    private final Environment environment;

    public VisitorImplementationInterpreter(Environment global, Environment environment) {
        this.global = global;
        this.environment = environment;
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

}
