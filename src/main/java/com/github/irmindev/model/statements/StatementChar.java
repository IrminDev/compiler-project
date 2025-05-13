package com.github.irmindev.model.statements;

import com.github.irmindev.model.Token;
import com.github.irmindev.model.expressions.Expression;

public class StatementChar extends StatementVar {
    public StatementChar(Token identifier, Expression initializer) {
        super(identifier, initializer);
    }    
}
