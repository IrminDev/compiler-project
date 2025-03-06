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

  private final Set<Character> singleCharTokens = Set.of('(', ')', '{', '}',
  ',', '-', '+', ';', '*', '/', '!', '='); 

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
    while (Character.isAlphabetic(peek()) || Character.isDigit(peek())) {
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

    int start = current; // Registro inicio

    // Estado 0, verififca si es la parte entera del numero
    while (Character.isDigit(peek())) { // Mientras lea o consuma dígitos.
      advance();
    }

    // Transición a Estado 1: verifico si hay punto decilam
    if (peek() == '.' ) { // Se confirma que es un decimal.
      if(Character.isDigit(peekNext())){
        advance(); // Consume el punto.
        while (Character.isDigit(peek())) { // Mientras lea o consuma dígitos.
          advance();
        }
      }else{
        throw new UnexpectedSymbolException(peek(), line);
      }
    }

    // Transición a Estado 2: verifica si hay una notacion
    if (peek() == 'E') {
      advance(); // Consume

      // Estado 3: Verifico signo (+/-) en exponente de notación científica.
      if (peek() == '+' || peek() == '-') {
        advance(); // Consumo el signo
      }

      // Estado 4: Verifico los dígitos del exponente
      if (!Character.isDigit(peek())) { // El exponente debe tener al menos un dígito.
        throw new UnexpectedSymbolException(peek(), line);
      }

      // Leo dígitos en el exponente.
      while (Character.isDigit(peek())) {
        advance();
      }
    }

    // Finalizo el análisis del número y devuelvo un token.
    String text = source.substring(start, current); // Extraigo el número completo.
    System.out.println(text);
    if(text.contains(".") || text.contains("E")){
      return new Token.ValueToken(TokenType.NUMBER, Double.parseDouble(text), line, text);
    }
    return new Token.ValueToken(TokenType.NUMBER, Integer.parseInt(text), line, text);
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
    if (peek() != '"') {
      throw new UnexpectedSymbolException(peek(), line);
    }
    int start = current; // Registro inicio de la cadena.
    advance(); // Consumimos la comilla inicial

    // *Estado 24: Consumimos los caracteres dentro de la cadena
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') {
        throw new UnexpectedSymbolException(peek(), line);
      }
      advance(); // Consumimos el carácter actual.
    }

    // Transición a Estado 25: Verificamos el cierre de la cadena.
    if (isAtEnd()) {
        throw new UnexpectedSymbolException(peek(), line);
    }

    advance(); // Consumimos la comilla final (`"`).

    String value = source.substring(start, current);

    return new Token.ValueToken(TokenType.STRING, source.substring(start+1, current-1), line, value);
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
    return true;
  }

  private boolean matchNext(char expected) {
    if (current + 1 >= length)
      return false;
    if (peekNext() != expected)
      return false;
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
    if(isAtEnd()){
      return '\0';
    }
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
    advance(); // Consumimos el primer caracter del comentario.
    // Verificamos si es un comentario comenzando con "/".
    if (match('/')) { // Coincide con "//". .
      // Estado 30: Comentario de una línea
      while (peek() != '\n' && !isAtEnd()) {
        advance(); // Consumimos todos los caracteres de la línea.
      }
      // Estado 31: Comentario termina en el salto de línea
      // Se ignoran los comentarios de una línea después de este punto.

    } else if (match('*')) { // Coincide con "/*".
      // Estado 27: Comentario multilínea
      while (!isAtEnd()) {
        if (peek() == '*' && peekNext() == '/') {
          // Estado 28: Fin del comentario multilínea con "*/"
          advance(); // Consumimos el '*'.
          advance(); // Consumimos el '/'.
          break;
        }
        advance(); // Leemos cada carácter dentro del comentario.
      }
    } else {
        throw new IllegalStateException("Unexpected comment :(");
    }
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
    return new Token.KeywordToken(type, line);
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
      advance();
    } else {
      type = switch (c) {
        case '<' -> TokenType.LESS;
        case '>' -> TokenType.GRATER;
        case '=' -> TokenType.EQUAL;
        case '!' -> TokenType.BANG;
        default -> throw new IllegalStateException("Unexpected relational operator :(");
      };
    }


    return new Token.KeywordToken(type, line);
  }
}
