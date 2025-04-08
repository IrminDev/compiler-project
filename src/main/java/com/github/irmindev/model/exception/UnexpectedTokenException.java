package com.github.irmindev.model.exception;

import com.github.irmindev.model.TokenType;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(TokenType expected, TokenType actual, int line) {
        super("Unexpected token: expected " + expected + ", but got " + actual + " at line " + line);
    }

    public UnexpectedTokenException(String expected, TokenType actual, int line) {
        super("Unexpected token: expected " + expected + ", but got " + actual + " at line " + line);
    }
}
