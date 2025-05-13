package com.github.irmindev.model.statements;

import com.github.irmindev.model.Token;
import com.github.irmindev.model.expressions.Expression;

public class StatementString extends StatementVar {
    public StatementString(Token identifier, Expression initializer) {
        super(identifier, initializer);
    }
}
