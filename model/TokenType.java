package model;

public enum TokenType {
    // Single-character tokens
    PLUS, MINUS, DOT, COMMA, SEMICOLON, SLASH, STAR, PERCENT, CARET, EQUAL, LEFT_PAREN,
    RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,

    // One or two character tokens
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    AND, ELSE, IF, FUN, FOR, IF, NULL, OR, PRINT, RETURN, VAR, WHILE, TRUE, FALSE,

    // End of file
    EOF
}