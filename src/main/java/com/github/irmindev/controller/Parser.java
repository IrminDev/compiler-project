package com.github.irmindev.controller;

import java.util.List;
import java.util.Set;

import com.github.irmindev.model.Token;
import com.github.irmindev.model.TokenType;
import com.github.irmindev.model.exception.UnexpectedTokenException;
import com.github.irmindev.model.expressions.Expression;
import com.github.irmindev.model.expressions.ExpressionLiteral;
import com.github.irmindev.model.statements.Statement;
import com.github.irmindev.model.statements.StatementBlock;
import com.github.irmindev.model.statements.StatementBoolean;
import com.github.irmindev.model.statements.StatementChar;
import com.github.irmindev.model.statements.StatementDouble;
import com.github.irmindev.model.statements.StatementExpression;
import com.github.irmindev.model.statements.StatementFunction;
import com.github.irmindev.model.statements.StatementIf;
import com.github.irmindev.model.statements.StatementInteger;
import com.github.irmindev.model.statements.StatementLoop;
import com.github.irmindev.model.statements.StatementPrint;
import com.github.irmindev.model.statements.StatementReturn;
import com.github.irmindev.model.statements.StatementString;
import com.github.irmindev.model.statements.StatementVar;

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
  
  private Token previous(){
    return tokens.get(currentTokenIndex - 1);
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
  private List<Statement> program() {
    List<Statement> statements = List.of();
    declaration(statements);
    return statements;
  }

  private void declaration(List<Statement> statements) {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.FUN:
        statements.add(functionDeclaration());
        declaration(statements);
        break;
      case TokenType.VAR:
        statements.add(varDeclaration());
        declaration(statements);
        break;
      default:
        if (statementTokens.contains(tokens.get(currentTokenIndex).getType())
          || expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
          statement();
          declaration(statements);
        }
        break;
    }

  }

  private Statement functionDeclaration() {
    match(TokenType.FUN);
    match(TokenType.IDENTIFIER);
    Token functionName = previous();
    match(TokenType.LEFT_PAREN);
    List<Token> params = parameters();
    match(TokenType.RIGHT_PAREN);
    StatementBlock body = block();
    return new StatementFunction(functionName, params, body);
  }

  private List<Token> parameters() {
    List<Token> parameters = List.of();
    if(tokens.get(currentTokenIndex).getType() == TokenType.IDENTIFIER) {
      match(TokenType.IDENTIFIER);
      parameters.add(previous());
      parametersOpt(parameters);
    }
    return parameters;
  }

  private void parametersOpt(List<Token> parameters) {
    if(tokens.get(currentTokenIndex).getType() == TokenType.COMMA) {
      match(TokenType.COMMA);
      match(TokenType.IDENTIFIER);
      parameters.add(previous());
      parametersOpt(parameters);
    }
  }

  private Statement varDeclaration() {
    match(TokenType.VAR);
    TokenType type = varTypeOpt();
    match(TokenType.IDENTIFIER);
    Token identifier = previous();
    Expression init = varInit();
    match(TokenType.SEMICOLON);
    switch (type) {
      case INTEGER_KW:
        return new StatementInteger(identifier, init);
      case DOUBLE_KW:
        return new StatementDouble(identifier, init);
      case STRING_KW:
        return new StatementString(identifier, init);
      case CHAR_KW:
        return new StatementChar(identifier, init);
      case BOOLEAN_KW:
        return new StatementBoolean(identifier, init);
      default:
        return new StatementVar(identifier, init);
    }
  }

  private TokenType varTypeOpt(){
    if (tokens.get(currentTokenIndex).getType() == TokenType.INTEGER_KW) {
      match(TokenType.INTEGER_KW);
      return TokenType.INTEGER_KW;
    } else if (tokens.get(currentTokenIndex).getType() == TokenType.DOUBLE_KW) {
      match(TokenType.DOUBLE_KW);
      return TokenType.DOUBLE_KW;
    } else if (tokens.get(currentTokenIndex).getType() == TokenType.STRING_KW) {
      match(TokenType.STRING_KW);
      return TokenType.STRING_KW;
    } else if (tokens.get(currentTokenIndex).getType() == TokenType.CHAR_KW) {
      match(TokenType.CHAR_KW);
      return TokenType.CHAR_KW;
    } else if (tokens.get(currentTokenIndex).getType() == TokenType.BOOLEAN_KW) {
      match(TokenType.BOOLEAN_KW);
      return TokenType.BOOLEAN_KW;
    } 
    return null;
  }

  private Expression varInit() {
    if (tokens.get(currentTokenIndex).getType() == TokenType.EQUAL) {
      match(TokenType.EQUAL);
      return expression();
    }
    return null;
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

   private Statement statement() {
    TokenType type = tokens.get(currentTokenIndex).getType();
    switch (type) {
      case TokenType.PRINT:
        return printStatement();
      case TokenType.IF:
        return ifStatement();
      case TokenType.FOR:
        return forStatement();
      case TokenType.WHILE:
        return whileStatement();
      case TokenType.RETURN:
        return returnStatement();
      case TokenType.LEFT_BRACE:
        return block();
      default:
        if(expressionTokens.contains(type)) {
          return expressionStatement();
        } else {
          throw new UnexpectedTokenException("Statement token", type, tokens.get(currentTokenIndex).getLine());
        }
      }
  }

  private Statement expressionStatement() {
    Expression exp = expression();
    match(TokenType.SEMICOLON);
    return new StatementExpression(exp);
  }

  private Statement printStatement() {
    match(TokenType.PRINT);
    Statement statement = new StatementPrint(expression());
    match(TokenType.SEMICOLON);
    return statement;
  }

  private Statement ifStatement() {
    match(TokenType.IF);
    match(TokenType.LEFT_PAREN);
    Expression cond = expression();
    match(TokenType.RIGHT_PAREN);
    Statement stm = statement();
    Statement elseStm = elseStatement();
    return new StatementIf(cond, stm, elseStm);
  }

  private Statement elseStatement() {
    if (tokens.get(currentTokenIndex).getType() == TokenType.ELSE) {
      match(TokenType.ELSE);
      return statement();
    }
    return null;
  }

  private Statement forStatement() {
    match(TokenType.FOR);
    match(TokenType.LEFT_PAREN);
    Statement init = forStatementInit();

    Expression cond = forStatementCond();

    Statement inc = new StatementExpression(forStatementInc());

    match(TokenType.RIGHT_PAREN);

    Statement body = statement();
    if (inc != null) {
      StatementBlock block = new StatementBlock(List.of(body, inc));
      body = block;
    }

    Statement loop = new StatementLoop(cond, body);

    if (init != null) {
      StatementBlock block = new StatementBlock(List.of(init, loop));
      return block;
    } else {
      return loop;
    }
  }

  private Statement forStatementInit(){
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.VAR:
        return varDeclaration();
      case TokenType.SEMICOLON:
        match(TokenType.SEMICOLON);
        return null;
      default:
        if(expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
          return expressionStatement();
        } else {
          throw new UnexpectedTokenException("Statement token", tokens.get(currentTokenIndex).getType(), tokens.get(currentTokenIndex).getLine());
        }
    }
  }

  private Expression forStatementCond(){
    if (tokens.get(currentTokenIndex).getType() == TokenType.SEMICOLON) {
      match(TokenType.SEMICOLON);
      return new ExpressionLiteral(true);
    } else if (expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
      Expression exp = expression();
      match(TokenType.SEMICOLON);
      return exp;
    } else {
      throw new UnexpectedTokenException("Statement token", tokens.get(currentTokenIndex).getType(), tokens.get(currentTokenIndex).getLine());
    }
  }

  private Expression forStatementInc(){
    if(expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
      return expression();
    }

    return null;
  }

  private Statement whileStatement() {
    match(TokenType.WHILE);
    match(TokenType.LEFT_PAREN);
    Expression cond = expression();
    match(TokenType.RIGHT_PAREN);
    Statement block = statement();
    return new StatementLoop(cond, block);
  }

  private Statement returnStatement() {
    match(TokenType.RETURN);
    StatementReturn rtn = new StatementReturn(returnExpOpt());
    match(TokenType.SEMICOLON);
    return rtn;
  }

  private Expression returnExpOpt() {
    if(expressionTokens.contains(tokens.get(currentTokenIndex).getType())) {
      return expression();
    }

    return null;
  }

  private StatementBlock block() {
    match(TokenType.LEFT_BRACE);
    List<Statement> statements = List.of();
    declaration(statements);
    match(TokenType.RIGHT_BRACE);
    return new StatementBlock(statements);
  }

  /**
   * Expressions section
   * 
   * @author Irmin
   */

  private Expression expression() {
    assignment();
    return null;
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
