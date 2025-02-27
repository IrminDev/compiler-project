package com.github.irmindev.model.exception;

public class UnexpectedSymbolException extends RuntimeException {
    public UnexpectedSymbolException(Character c, int line) {
        super("Unexpected character: " + c + " at line " + line);
    }
}