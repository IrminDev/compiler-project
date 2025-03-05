package com.github.irmindev.controller;

import com.github.irmindev.model.Token;
import com.github.irmindev.model.TokenType;
import com.github.irmindev.model.exception.UnexpectedSymbolException;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Lexer {
  private final String source;
  private final int length;
  private int current = 0;
  private int line = 1;

  // private final Set<Character> singleCharTokens = Set.of('(', ')', '{', '}',
  // ',', '-', '+', ';', '*', '/', '!', '=',
  // '<', '>');

  private final Set<Character> whiteSpaces = Set.of(' ', '\r', '\t', '\n');

  public Lexer(String source) {
    this.source = source;
    this.length = source.length();
  }

  public List<Token> scanTokens() {
    List<Token> tokens = new ArrayList<>();

    while (!isAtEnd()) {
      if (Character.isAlphabetic(peek())) {
        tokens.add(alphLexeme());
      } else if (Character.isDigit(peek())) {
        tokens.add(number());
      } else if (match('"')) {
        tokens.add(string());
      } else if (whiteSpaces.contains(peek())) {
        skipWhiteSpaces();
      } else if (match('/')) {
        if (matchNext('/')) {
          skipComment();
        } else if (matchNext('*')) {
          skipComment();
        } else {
          tokens.add(singleChar());
        }
      } else if (singleCharTokens.contains(peek())) {
        tokens.add(singleChar());
      } else if (match('<') || match('>') || match('=') || match('!')) {
        tokens.add(relationalOperator());
      } else {
        throw new UnexpectedSymbolException(peek(), line);
      }

      advance();
    }

    tokens.add(new Token.EOFToken(TokenType.EOF, "$"));

    return tokens;
  }

  /**
   * This method is used to handle the case of identifiers, keywords and known
   * values
   * 
   * @author Irmin
   * @return
   */
  private Token alphLexeme() {
    int start = current;
    while (Character.isAlphabetic(peekNext()) || Character.isDigit(peekNext())) {
      advance();
    }
    String text = source.substring(start, current);
    TokenType type = identifierType(text);
    if (type == TokenType.IDENTIFIER) {
      return new Token.Indetifier(type, text, line);
    } else if (type == TokenType.TRUE || type == TokenType.FALSE) {
      return new Token.KnownValueToken(type, Boolean.parseBoolean(text), line);
    } else if (type == TokenType.NULL) {
      return new Token.KnownValueToken(type, null, line);
    } else {
      return new Token.KeywordToken(type, line);
    }
  }

  private TokenType identifierType(String text) {
    switch (text) {
      case "else":
        return TokenType.ELSE;
      case "false":
        return TokenType.FALSE;
      case "for":
        return TokenType.FOR;
      case "fun":
        return TokenType.FUN;
      case "if":
        return TokenType.IF;
      case "null":
        return TokenType.NULL;
      case "print":
        return TokenType.PRINT;
      case "return":
        return TokenType.RETURN;
      case "true":
        return TokenType.TRUE;
      case "var":
        return TokenType.VAR;
      case "while":
        return TokenType.WHILE;
      default:
        return TokenType.IDENTIFIER;
    }
  }

  /**
   * This method is used to handle the case of numbers
   * such as 123, 3.14, 0.123, 123.0 (check the transition diagram for more
   * details)
   * 
   * @author Angel
   * @return Token
   */
  private Token number() {

  }

  /**
   * This method is used to handle the case of string literals
   * such as "Hello, World!" or "123" (check the transition diagram for more
   * details)
   * 
   * @author Angel
   * @return Token
   */
  private Token string() {

  }

  private boolean isAtEnd() {
    return current >= length;
  }

  private char peek() {
    if (isAtEnd())
      return '\0';
    return source.charAt(current);
  }

  private boolean match(char expected) {
    if (isAtEnd())
      return false;
    if (peek() != expected)
      return false;
    current++;
    return true;
  }

  private boolean matchNext(char expected) {
    if (current + 1 >= length)
      return false;
    if (peekNext() != expected)
      return false;
    current++;
    return true;
  }

  /**
   * This method is used to handle the case of white spaces
   * such as ' ', '\r', '\t', '\n'
   * 
   * @author Rodolfo
   * @return void
   */
  private void skipWhiteSpaces() {
    while (!isAtEnd() && whiteSpaces.contains(peek())) {
      if (peek() == '\n') {
        line++;
      }
      advance();
    }
  }

  private char advance() {
    return source.charAt(current++);
  }

  private char peekNext() {
    if (current + 1 >= length)
      return '\0';
    return source.charAt(current + 1);
  }

  /**
   * This method is used to handle the case of comments
   * such as //, /*, * / (check the transition diagram for more details)
   * 
   * @author Angel
   * @return Token
   */
  private void skipComment() {

  }

  /**
   * This method is used to handle the case of single character tokens
   * such as (, ), {, }, ,, ., -, +, ;, *, /, %, !, =, <, >, &, |, ^, ~
   * 
   * @author Rodolfo
   * @return Token
   */
  private Token singleChar() {
    char c = advance();
    TokenType type = TokenType.fromChar(c);
    return new Token.ValueToken(type, null, line, String.valueOf(c));
  }

  /**
   * This method is used to handle the case of relational operators
   * such as <, >, <=, >=, ==, !=
   * 
   * @author Rodolfo
   * @return Token
   */
  private Token relationalOperator() {
    char c = advance();
    TokenType type;

    if (match('=')) {
      type = switch (c) {
        case '<' -> TokenType.LESS_EQUAL;
        case '>' -> TokenType.GRATER_EQUAL;
        case '=' -> TokenType.EQUAL_EQUAL;
        case '!' -> TokenType.BANG_EQUAL;
        default -> throw new IllegalStateException("Unexpected relational operator :(");
      };
    } else {
      type = switch (c) {
        case '<' -> TokenType.LESS;
        case '>' -> TokenType.GRATER;
        case '=' -> TokenType.EQUAL;
        case '!' -> TokenType.BANG;
        default -> throw new IllegalStateException("Unexpected relational operator :(");
      };
    }

    return new Token.ValueToken(type, null, line, String.valueOf(c));
  }
}
