package com.github.irmindev.model;

public enum TokenType {
    // Reserved words
    ELSE, FALSE, FOR, FUN, IF, NULL, PRINT, RETURN, TRUE, VAR, WHILE,

    // Operators (Arithmetic)
    PLUS, MINUS, STAR, SLASH,

    // Operators (Relational)
    LESS, GRATER, LESS_EQUAL, GRATER_EQUAL, EQUAL_EQUAL, BANG_EQUAL,

    // Operators (Logical)
    AND, OR,

    // Operators (Assignment)
    EQUAL,

    // Operators (Negation)
    BANG,

    // Punctuation
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, SEMICOLON,

    // Data types
    IDENTIFIER, STRING, NUMBER,

    // End of file
    EOF
}