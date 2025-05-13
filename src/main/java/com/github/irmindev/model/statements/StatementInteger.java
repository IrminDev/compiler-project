package com.github.irmindev.model.statements;

import com.github.irmindev.model.Token;
import com.github.irmindev.model.expressions.Expression;

public class StatementInteger extends StatementVar {
    public StatementInteger(Token identifier, Expression initializer) {
        super(identifier, initializer);
    }
}
