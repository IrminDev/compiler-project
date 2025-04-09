package com.github.irmindev.controller;

import java.util.List;
import java.util.Set;

import com.github.irmindev.model.Token;
import com.github.irmindev.model.TokenType;
import com.github.irmindev.model.exception.UnexpectedTokenException;

public class Parser {
  private final List<Token> tokens;
  private int currentTokenIndex = 0;

  private Set<TokenType> expressionTokens = Set.of(
    TokenType.BANG,
    TokenType.MINUS,
    TokenType.TRUE,
    TokenType.FALSE,
    TokenType.NULL,
    TokenType.INTEGER,
    TokenType.DOUBLE,
    TokenType.STRING,
    TokenType.IDENTIFIER,
    TokenType.CHAR,
    TokenType.LEFT_PAREN
  );

  private Set<TokenType> statementTokens = Set.of(
    TokenType.FOR,
    TokenType.WHILE,
    TokenType.PRINT,
    TokenType.IF,
    TokenType.RETURN,
    TokenType.LEFT_BRACE
  );

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   * Parses the tokens and builds an abstract syntax tree (AST) **PENDING.
   * This method is the top-level entry point for parsing the tokens.
   */
  public void parse() {
    program();
    match(TokenType.EOF);
  }

  /**
   * Program, declarations and others section
   * 
   * @author Rodolfo
   */
  private void program() {
    declaration();
  }

  private void declaration() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.FUN:
        functionDeclaration();
        declaration();
        break;
      case TokenType.VAR:
        varDeclaration();
        declaration();
        break;
      default:
        if (statementTokens.contains(tokens.get(currentTokenIndex).getType())
          || expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
          statement();
          declaration();
        }
        break;
    }

  }

  private void functionDeclaration() {
    match(TokenType.FUN);
    match(TokenType.IDENTIFIER);
    match(TokenType.LEFT_PAREN);
    parameters();
    match(TokenType.RIGHT_PAREN);
    block();
  }

  private void parameters() {
    if(tokens.get(currentTokenIndex).getType() == TokenType.IDENTIFIER) {
      match(TokenType.IDENTIFIER);
      parametersOpt();
    }
  }

  private void parametersOpt() {
    if(tokens.get(currentTokenIndex).getType() == TokenType.COMMA) {
      match(TokenType.COMMA);
      match(TokenType.IDENTIFIER);
      parametersOpt();
    }
  }

  private void varDeclaration() {
    match(TokenType.VAR);
    varTypeOpt();
    match(TokenType.IDENTIFIER);
    varInit();
    match(TokenType.SEMICOLON);
  }

  private void varTypeOpt(){
    if (tokens.get(currentTokenIndex).getType() == TokenType.INTEGER_KW) {
      match(TokenType.INTEGER_KW);
    } else if (tokens.get(currentTokenIndex).getType() == TokenType.DOUBLE_KW) {
      match(TokenType.DOUBLE_KW);
    } else if (tokens.get(currentTokenIndex).getType() == TokenType.STRING_KW) {
      match(TokenType.STRING_KW);
    } else if (tokens.get(currentTokenIndex).getType() == TokenType.CHAR_KW) {
      match(TokenType.CHAR_KW);
    } else if (tokens.get(currentTokenIndex).getType() == TokenType.BOOLEAN_KW) {
      match(TokenType.BOOLEAN_KW);
    } 
  }

  private void varInit() {
    if (tokens.get(currentTokenIndex).getType() == TokenType.EQUAL) {
      match(TokenType.EQUAL);
      expression();
    }
  }

  private void arguments() {
    if (tokens.get(currentTokenIndex).getType() != TokenType.RIGHT_PAREN) {
      expression();
      argumentsOpt();
    }
  }

  private void argumentsOpt() {
    if (tokens.get(currentTokenIndex).getType() == TokenType.COMMA) {
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

   private void statement() {
    TokenType type = tokens.get(currentTokenIndex).getType();
    switch (type) {
      case TokenType.PRINT:
        printStatement();
        break;
      case TokenType.IF:
        ifStatement();
        break;
      case TokenType.FOR:
        forStatement();
        break;
      case TokenType.WHILE:
        whileStatement();
        break;
      case TokenType.RETURN:
        returnStatement();
        break;
      case TokenType.LEFT_BRACE:
        block();
        break;
      default:
        if(expressionTokens.contains(type)) {
          expressionStatement();
        } else {
          throw new UnexpectedTokenException("Statement token", type, tokens.get(currentTokenIndex).getLine());
        }
        break;
    }
  }

  private void expressionStatement() {
    expression();
    match(TokenType.SEMICOLON);
  }

  private void printStatement() {
    match(TokenType.PRINT);
    expression();
    match(TokenType.SEMICOLON);
  }

  private void ifStatement() {
    match(TokenType.IF);
    match(TokenType.LEFT_PAREN);
    expression();
    match(TokenType.RIGHT_PAREN);
    statement();
    elseStatement();
  }

  private void elseStatement() {
    if (tokens.get(currentTokenIndex).getType() == TokenType.ELSE) {
      match(TokenType.ELSE);
      statement();
    }
  }

  private void forStatement() {
    match(TokenType.FOR);
    match(TokenType.LEFT_PAREN);

    forStatementInit();

    forStatementCond();

    forStatementInc();

    match(TokenType.RIGHT_PAREN);

    statement();
  }

  private void forStatementInit(){
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.VAR:
        varDeclaration();
        break;
      case TokenType.SEMICOLON:
        match(TokenType.SEMICOLON);
        break;
      default:
        if(expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
          expressionStatement();
        } else {
          throw new UnexpectedTokenException("Statement token", tokens.get(currentTokenIndex).getType(), tokens.get(currentTokenIndex).getLine());
        }
        break;
    }
  }

  private void forStatementCond(){
    if (tokens.get(currentTokenIndex).getType() == TokenType.SEMICOLON) {
      match(TokenType.SEMICOLON);
    } else if (expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
      expression();
      match(TokenType.SEMICOLON);
    } else {
      throw new UnexpectedTokenException("Statement token", tokens.get(currentTokenIndex).getType(), tokens.get(currentTokenIndex).getLine());
    }
  }

  private void forStatementInc(){
    if(expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
      expression();
    }
  }

  private void whileStatement() {
    match(TokenType.WHILE);
    match(TokenType.LEFT_PAREN);
    expression();
    match(TokenType.RIGHT_PAREN);
    statement();
  }

  private void returnStatement() {
    match(TokenType.RETURN);
    returnExpOpt();
    match(TokenType.SEMICOLON);
  }

  private void returnExpOpt() {
    if(expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
      expression();
    }
  }

  private void block() {
    match(TokenType.LEFT_BRACE);
    
    declaration();
    
    match(TokenType.RIGHT_BRACE);
  }

  /**
   * Expressions section
   * 
   * @author Irmin
   */

  private void expression() {
    assignment();
  }

  private void assignment() {
    logicOr();
    assignmentOpc();
  }

  private void assignmentOpc() {
    if (TokenType.EQUAL.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.EQUAL);
      assignment();
    }
  }

  private void logicOr() {
    logicAnd();
    logicOrOpt();
  }

  private void logicOrOpt() {
    if (TokenType.OR.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.OR);
      logicOr();
    }
  }

  private void logicAnd() {
    equality();
    logicAndOpt();
  }

  private void logicAndOpt() {
    if (TokenType.AND.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.AND);
      logicAnd();
    }
  }

  private void equality() {
    comparison();
    equalityOpt();
  }

  private void equalityOpt() {
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

  private void comparison() {
    term();
    comparisonOpt();
  }

  private void comparisonOpt() {
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

  private void term() {
    factor();
    termOpt();
  }

  private void termOpt() {
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

  private void factor() {
    unary();
    factorOpt();
  }

  private void factorOpt() {
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

  private void unary() {
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
        call();
        break;
    }
  }

  // Modified to instead of a primary, it must to be an identifier to be a call
  private void call() {
    primary();
    if(tokens.get(currentTokenIndex-1).getType() == TokenType.IDENTIFIER) {
      callOpt();
    }
  }

  private void callOpt() {
    if (TokenType.LEFT_PAREN.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.LEFT_PAREN);
      arguments();
      match(TokenType.RIGHT_PAREN);
    }
  }

  private void primary() {
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
      case TokenType.LEFT_PAREN:
        match(TokenType.LEFT_PAREN);
        expression();
        match(TokenType.RIGHT_PAREN);
        break;
      default:
        throw new UnexpectedTokenException("Value token", tokens.get(currentTokenIndex).getType(), tokens.get(currentTokenIndex).getLine());
    }
  }

  /**
   * Utility methods
   * 
   * @author Irmin
   */

  private void match(TokenType expectedType) {
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
