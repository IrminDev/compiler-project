package com.github.irmindev.model.statements;

import java.util.List;

import com.github.irmindev.model.Token;

public class StatementFunction extends Statement{
    private final Token identifier;
    private final StatementBlock block;
    private final List<Token> parameters;

    public StatementFunction(Token identifier, List<Token> parameters, StatementBlock block) {
        this.identifier = identifier;
        this.parameters = parameters;
        this.block = block;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public StatementBlock getBlock() {
        return block;
    }

    public List<Token> getParameters() {
        return parameters;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
