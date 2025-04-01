package com.github.irmindev.model.exception;

import com.github.irmindev.model.TokenType;

public class UnexpectedTokenException extends RuntimeException {
    private TokenType expected;
    private TokenType actual;
    private int line;

    public UnexpectedTokenException(TokenType expected, TokenType actual, int line) {
        super("Unexpected token: expected " + expected + ", but got " + actual + " at line " + line);
        this.expected = expected;
        this.actual = actual;
        this.line = line;
    }
}
