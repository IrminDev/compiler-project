package com.github.irmindev.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.irmindev.model.Token;
import com.github.irmindev.model.TokenType;
import com.github.irmindev.model.Token.ValueToken;
import com.github.irmindev.model.exception.UnexpectedTokenException;
import com.github.irmindev.model.expressions.Expression;
import com.github.irmindev.model.expressions.ExpressionArit;
import com.github.irmindev.model.expressions.ExpressionUnary;
import com.github.irmindev.model.expressions.ExpressionVariable;
import com.github.irmindev.model.expressions.ExpressionLiteral;
import com.github.irmindev.model.expressions.ExpressionRelational;
import com.github.irmindev.model.expressions.ExpressionLogical;
import com.github.irmindev.model.expressions.ExpressionCallFunction;
import com.github.irmindev.model.expressions.ExpressionAssign;
import com.github.irmindev.model.expressions.ExpressionGrouping;
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
    List<Statement> statements = new ArrayList<>();
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
    List<Token> parameters = new ArrayList<>();
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
    if(type == null) {
      type = TokenType.VAR; // Default type if no type is specified
    }
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

  private List<Expression> arguments() {
    List<Expression> arguments = new ArrayList<>();
    if (tokens.get(currentTokenIndex).getType() != TokenType.RIGHT_PAREN) {
      arguments.add(expression());
      argumentsOpt(arguments);
    }
    return arguments;
  }

  private void argumentsOpt(List<Expression> arguments) {
    if (tokens.get(currentTokenIndex).getType() == TokenType.COMMA) {
      match(TokenType.COMMA);
      arguments.add(expression());
      argumentsOpt(arguments);
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
    List<Statement> statements = new ArrayList<>();
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
    Expression left = assignment();
    return left;
  }

  private Expression assignment() {
    Expression left = logicOr();
    return assignmentOpc(left);
  }

  private Expression assignmentOpc(Expression left) {
    if (TokenType.EQUAL.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.EQUAL);
      Token name = previous();
      Expression value = logicOr();
      return assignmentOpc(new ExpressionAssign(name, value ));
    }
    return left;
  }

  private Expression logicOr() {
    Expression left = logicAnd();
    return logicOrOpt(left);
  }

  private Expression logicOrOpt(Expression left) {
    if (TokenType.OR.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.OR);
      Token operator = previous();
      Expression right = logicAnd();
      Expression join =  new ExpressionLogical(left, operator, right);
      return logicOrOpt(join);
    }
    return left;
  }

  private Expression logicAnd() {
    Expression left =  equality();
    return logicAndOpt(left);
  }

  private Expression logicAndOpt(Expression left) {
    if (TokenType.AND.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.AND);
      Token operator = previous();
      Expression right = equality();
      Expression join = new ExpressionLogical(left, operator, right);
      return logicAndOpt(join);
    }
   return left;
  }

  private Expression equality() {
   Expression left =  comparison();
    return equalityOpt(left);
  }

  private Expression equalityOpt(Expression left) {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.EQUAL_EQUAL: {
        match(TokenType.EQUAL_EQUAL);
        Token operator = previous();
        Expression right = comparison();
        return equalityOpt(new ExpressionRelational(left, operator, right));
      }
      case TokenType.BANG_EQUAL: {
        match(TokenType.BANG_EQUAL);
        Token operator = previous();
        Expression right = comparison();
        return equalityOpt(new ExpressionRelational(left, operator, right));
      }
      default:
        return left;
    }
  }

  private Expression comparison() {
    Expression left = term();
    return comparisonOpt(left);
  }

  private Expression comparisonOpt(Expression left) {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.GREATER: {
        match(TokenType.GREATER);
        Token operator = previous();
        Expression right = term();
        return comparisonOpt(new ExpressionRelational(left, operator, right));
      }
      case TokenType.GREATER_EQUAL: {
        match(TokenType.GREATER_EQUAL);
        Token operator = previous();
        Expression right = term();
        return comparisonOpt(new ExpressionRelational(left, operator, right));
      }
      case TokenType.LESS: {
        match(TokenType.LESS);
        Token operator = previous();
        Expression right = term();
        return comparisonOpt(new ExpressionRelational(left, operator, right));
      }
      case TokenType.LESS_EQUAL:
        match(TokenType.LESS_EQUAL);
        Token operator = previous();
        Expression right = term();
        return comparisonOpt(new ExpressionRelational(left,operator,right));
      default:
        return left;
    }
  }

  private Expression term() {
    Expression left = factor();
    return termOpt(left);
  }

  private Expression  termOpt(Expression left) {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.PLUS: {
        match(TokenType.PLUS);
        Token operator = previous();
        Expression right = factor();
        return termOpt(new ExpressionArit(left,operator, right));
      }
      case TokenType.MINUS: {
        match(TokenType.MINUS);
        Token operator = previous();
        Expression right = factor();
        return termOpt(new ExpressionArit(left, operator, right));
      }
      default:
        return left;
    }
  }

  private Expression factor() {
    Expression left = unary();
    return factorOpt(left);
  }

  private Expression factorOpt(Expression left) {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.STAR: {
        match(TokenType.STAR);
        Token operator = previous();
        Expression right = unary();
        return factorOpt(new ExpressionArit(left, operator, right));
      }
      case TokenType.SLASH:{
        match(TokenType.SLASH);
        Token operator = previous();
        Expression right = unary();
        return factorOpt(new ExpressionArit(left, operator, right));
        }
        default:
        return left;
    }
  }

  private Expression unary() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.BANG: {
        match(TokenType.BANG);
        Token operator = previous();
        Expression exp = unary();
        return new ExpressionUnary(operator, exp);
      }
      case TokenType.MINUS: {
        match(TokenType.MINUS);
        Token operator = previous();
        Expression exp = unary();
        return new ExpressionUnary(operator, exp);
      }
      default:
        return call();
    }
  }

//DUDA con respecto a la verificación del siguiete token

  // Modified to instead of a primary, it must to be an identifier to be a call
  private Expression call() {
    Expression exp = primary();
    if(exp instanceof ExpressionVariable
        && tokens.get(currentTokenIndex).getType() == TokenType.LEFT_PAREN) {
      List<Expression> args = callOpt();
      return new ExpressionCallFunction(exp, args);
    }
    return exp;
  }

  private List<Expression> callOpt() {
    if (TokenType.LEFT_PAREN.equals(tokens.get(currentTokenIndex).getType())) {
      match(TokenType.LEFT_PAREN);
      List<Expression> args = arguments();
      match(TokenType.RIGHT_PAREN);
      return args;
    }
    return null;
  }

  private Expression primary() {
    switch (tokens.get(currentTokenIndex).getType()) {
      case TokenType.INTEGER:
        match(TokenType.INTEGER);
        ValueToken previous = (ValueToken)previous();
        Object value = previous.getLiteral();
        return new ExpressionLiteral(value);
      case TokenType.DOUBLE:
        match(TokenType.DOUBLE);
        ValueToken previousDouble = (ValueToken)previous();
        Object valueDouble = previousDouble.getLiteral();
        return new ExpressionLiteral(valueDouble);
      case TokenType.STRING:
        match(TokenType.STRING);
        ValueToken previousString = (ValueToken)previous();
        Object valueString = previousString.getLiteral();
        return new ExpressionLiteral(valueString);
      case TokenType.TRUE:
        match(TokenType.TRUE);
        return new ExpressionLiteral(true);
      case TokenType.FALSE:
        match(TokenType.FALSE);
        return new ExpressionLiteral(false);
      case TokenType.NULL:
        match(TokenType.NULL);
        return new ExpressionLiteral(null);
      case TokenType.IDENTIFIER:
        match(TokenType.IDENTIFIER);
        Token TokenID = previous();
        return new ExpressionVariable((Token.Indetifier)TokenID);
      case TokenType.CHAR:
        match(TokenType.CHAR);
        ValueToken previousChar = (ValueToken)previous();
        Object valueChar = previousChar.getLiteral();
        return new ExpressionLiteral(valueChar);
      case TokenType.LEFT_PAREN:
        match(TokenType.LEFT_PAREN);
        ExpressionGrouping exp = new ExpressionGrouping(expression());
        match(TokenType.RIGHT_PAREN);
        return exp;
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
