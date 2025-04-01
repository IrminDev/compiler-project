package com.github.irmindev.model;

public enum TokenType {
  // Reserved words
  ELSE, FALSE, FOR, FUN, IF, NULL, PRINT, RETURN, TRUE, VAR, WHILE,

  // Operators (Arithmetic)
  PLUS, MINUS, STAR, SLASH,

  // Operators (Relational)
  LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, EQUAL_EQUAL, BANG_EQUAL,

  // Operators (Logical)
  AND, OR,

  // Operators (Assignment)
  EQUAL,

  // Operators (Negation)
  BANG,

  // Punctuation
  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, SEMICOLON,

  // Data types
  IDENTIFIER, STRING,

  // End of file
  EOF,

  // Data types
  INTEGER, DOUBLE, CHAR,
  
  // Data kwywords
  INTEGER_KW, DOUBLE_KW ,BOOLEAN_KW, STRING_KW, CHAR_KW;

  public static TokenType fromChar(char c) {
    return switch (c) {
      case '+' -> PLUS;
      case '-' -> MINUS;
      case '*' -> STAR;
      case '/' -> SLASH;
      case '<' -> LESS;
      case '>' -> GREATER;
      case '=' -> EQUAL;
      case '!' -> BANG;
      case '(' -> LEFT_PAREN;
      case ')' -> RIGHT_PAREN;
      case '{' -> LEFT_BRACE;
      case '}' -> RIGHT_BRACE;
      case ',' -> COMMA;
      case ';' -> SEMICOLON;
      default -> throw new IllegalArgumentException("Unexpected character: " + c);
    };
  }

  //
}
