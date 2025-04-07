package com.github.irmindev.controller;

import java.util.List;

import com.github.irmindev.model.Token;
import com.github.irmindev.model.TokenType;
import com.github.irmindev.model.exception.UnexpectedTokenException;

public class Parser {
  private final List<Token> tokens;
  private int currentTokenIndex = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   * Parses the tokens and builds an abstract syntax tree (AST) **PENDING.
   * This method is the top-level entry point for parsing the tokens.
   */
  public void parse() {

  }

  /**
   * Program, declarations and others section
   * 
   * @author Rodolfo
   */
  public void program() {
    declaration();
  }

  public void declaration() {
    while (currentTokenIndex < tokens.size()) {
      if (tokens.get(currentTokenIndex).getType() == TokenType.FUN) {
        functionDeclaration();
      } else if (tokens.get(currentTokenIndex).getType() == TokenType.VAR) {
        varDeclaration();
      } else {
        statement();
      }
    }
  }

  public void functionDeclaration() {
    match(TokenType.FUN);
    match(TokenType.IDENTIFIER);
    match(TokenType.LEFT_PAREN);
    parameters();
    match(TokenType.RIGHT_PAREN);
    block();
  }

  public void parameters() {
    if (tokens.get(currentTokenIndex).getType() != TokenType.RIGHT_PAREN) {
      match(TokenType.IDENTIFIER);
      parametersOpt();
    }
  }

  public void parametersOpt() {
    while (tokens.get(currentTokenIndex).getType() == TokenType.COMMA) {
      match(TokenType.COMMA);
      match(TokenType.IDENTIFIER);
      parametersOpt();
    }
  }

  public void varDeclaration() {
    match(TokenType.VAR);
    match(TokenType.IDENTIFIER);
    varInit();
    match(TokenType.SEMICOLON);
  }

  public void varInit() {
    if (tokens.get(currentTokenIndex).getType() == TokenType.EQUAL) {
      match(TokenType.EQUAL);
      expression();
    }
  }

  public void arguments() {
    if (tokens.get(currentTokenIndex).getType() != TokenType.RIGHT_PAREN) {
      expression();
      argumentsOpt();
    }
  }

  public void argumentsOpt() {
    while (tokens.get(currentTokenIndex).getType() == TokenType.COMMA) {
      match(TokenType.COMMA);
      expression();
      argumentsOpt();
    }
  }

  /**
   * Sentences section
   * 
   * @author Angel
   */

  public void statement() {
    TokenType type = tokens.get(currentTokenIndex).getType();
    switch (type) {
      case PRINT:
        printStatement();
        break;
      case IF:
        ifStatement();
        break;
      case FOR:
        forStatement();
        break;
      case WHILE:
        whileStatement();
        break;
      case RETURN:
        returnStatement();
        break;
      case LEFT_BRACE:
        block();
        break;
      default:
        expressionStatement();
        break;
    }
  }

  public void expressionStatement() {
    expression();
    match(TokenType.SEMICOLON);
  }

  public void printStatement() {
    match(TokenType.PRINT);
    expression();
    match(TokenType.SEMICOLON);
  }

  public void ifStatement() {
    match(TokenType.IF);
    match(TokenType.LEFT_PAREN);
    expression();
    match(TokenType.RIGHT_PAREN);
    statement();
    if (tokens.get(currentTokenIndex).getType() == TokenType.ELSE) {
      match(TokenType.ELSE);
      statement();
    }
  }

  public void forStatement() {
    match(TokenType.FOR);
    match(TokenType.LEFT_PAREN);

    if (tokens.get(currentTokenIndex).getType() == TokenType.VAR) {
      varDeclaration();
    } else if (tokens.get(currentTokenIndex).getType() != TokenType.SEMICOLON) {
      expressionStatement();
    } else {
      match(TokenType.SEMICOLON);
    }

    if (tokens.get(currentTokenIndex).getType() != TokenType.SEMICOLON) {
      expression();
    }
    match(TokenType.SEMICOLON);

    if (tokens.get(currentTokenIndex).getType() != TokenType.RIGHT_PAREN) {
      expression();
    }
    match(TokenType.RIGHT_PAREN);

    statement();
  }

  public void whileStatement() {
    match(TokenType.WHILE);
    match(TokenType.LEFT_PAREN);
    expression();
    match(TokenType.RIGHT_PAREN);
    statement();
  }

  public void returnStatement() {
    match(TokenType.RETURN);
    if (tokens.get(currentTokenIndex).getType() != TokenType.SEMICOLON) {
      expression();
    }
    match(TokenType.SEMICOLON);
  }

  public void block() {
    match(TokenType.LEFT_BRACE);
    while (tokens.get(currentTokenIndex).getType() != TokenType.RIGHT_BRACE) {
      statement();
    }
    match(TokenType.RIGHT_BRACE);
  }

  /**
   * Expressions section
   * 
   * @author Irmin
   */

  public void expression() {
    assignment();
  }

  public void assignment() {
    logicOr();
    assignmentOpc();
  }

  public void assignmentOpc() {
    if (TokenType.EQUAL.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.EQUAL);
      assignment();
    }
  }

  public void logicOr() {
    logicAnd();
    logicOrOpt();
  }

  public void logicOrOpt() {
    if (TokenType.OR.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.OR);
      logicOr();
    }
  }

  public void logicAnd() {
    equality();
    logicAndOpt();
  }

  public void logicAndOpt() {
    if (TokenType.AND.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.AND);
      logicAnd();
    }
  }

  public void equality() {
    comparison();
    equalityOpt();
  }

  public void equalityOpt() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.EQUAL_EQUAL:
        match(TokenType.EQUAL_EQUAL);
        equality();
        break;
      case TokenType.BANG_EQUAL:
        match(TokenType.BANG_EQUAL);
        equality();
        break;
      default:
        break;
    }
  }

  public void comparison() {
    term();
    comparisonOpt();
  }

  public void comparisonOpt() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.GREATER:
        match(TokenType.GREATER);
        comparison();
        break;
      case TokenType.GREATER_EQUAL:
        match(TokenType.GREATER_EQUAL);
        comparison();
        break;
      case TokenType.LESS:
        match(TokenType.LESS);
        comparison();
        break;
      case TokenType.LESS_EQUAL:
        match(TokenType.LESS_EQUAL);
        comparison();
        break;
      default:
        break;
    }
  }

  public void term() {
    factor();
    termOpt();
  }

  public void termOpt() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.PLUS:
        match(TokenType.PLUS);
        term();
        break;
      case TokenType.MINUS:
        match(TokenType.MINUS);
        term();
        break;
      default:
        break;
    }
  }

  public void factor() {
    unary();
    factorOpt();
  }

  public void factorOpt() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.STAR:
        match(TokenType.STAR);
        factor();
        break;
      case TokenType.SLASH:
        match(TokenType.SLASH);
        factor();
        break;
      default:
        break;
    }
  }

  public void unary() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.BANG:
        match(TokenType.BANG);
        unary();
        break;
      case TokenType.MINUS:
        match(TokenType.MINUS);
        unary();
        break;
      default:
        break;
    }
  }

  public void call() {
    primary();
    callOpt();
  }

  public void callOpt() {
    if (TokenType.LEFT_PAREN.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.LEFT_PAREN);
      arguments();
      match(TokenType.RIGHT_PAREN);
    }
  }

  public void primary() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.INTEGER:
        match(TokenType.INTEGER);
        break;
      case TokenType.DOUBLE:
        match(TokenType.DOUBLE);
        break;
      case TokenType.STRING:
        match(TokenType.STRING);
        break;
      case TokenType.TRUE:
        match(TokenType.TRUE);
        break;
      case TokenType.FALSE:
        match(TokenType.FALSE);
        break;
      case TokenType.NULL:
        match(TokenType.NULL);
        break;
      case TokenType.IDENTIFIER:
        match(TokenType.IDENTIFIER);
        break;
      case TokenType.CHAR:
        match(TokenType.CHAR);
        break;
      default:
        break;
    }
  }

  /**
   * Utility methods
   * 
   * @author Irmin
   */

  public void match(TokenType expectedType) {
    if (currentTokenIndex < tokens.size()) {
      Token currentToken = tokens.get(currentTokenIndex);
      if (currentToken.getType().equals(expectedType)) {
        currentTokenIndex++;
      } else {
        throw new UnexpectedTokenException(expectedType, currentToken.getType(), currentToken.getLine());
      }
    } else {
      throw new RuntimeException("Unexpected end of input");
    }
  }

}
