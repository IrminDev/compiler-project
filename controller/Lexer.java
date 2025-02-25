package controller;

import model.Token;
import model.TokenType;
import java.util.List;
import java.util.ArrayList;

public class Lexer{
    private final String source;
    private final int length;
    private int current = 0;
    private int line = 1;

    public Scanner(String source){
        this.source = source;
        this.length = source.length();
    }

    public List<Token> scanTokens(){
        List<Token> tokens = new ArrayList<>();


        return tokens;
    }

    private Token next(){

    }

    private Token identifier(){

    }

    private TokenType identifierType(String text){
        switch(text){
            case "and": return TokenType.AND;
            case "else": return TokenType.ELSE;
            case "false": return TokenType.FALSE;
            case "for": return TokenType.FOR;
            case "fun": return TokenType.FUN;
            case "if": return TokenType.IF;
            case "null": return TokenType.NULL;
            case "or": return TokenType.OR;
            case "print": return TokenType.PRINT;
            case "return": return TokenType.RETURN;
            case "var": return TokenType.VAR;
            case "while": return TokenType.WHILE;
            case "true": return TokenType.TRUE;
            default: return TokenType.IDENTIFIER;
        }
    }

    private Token number(){

    }

    private Token string(){

    }

    private boolean match(char expected){

    }

    private boolean isAtEnd(){
        return current >= length;
    }

    private char peek(){
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private void skipWhiteSpaces(){

    }

    private char advance(){
        return source.charAt(current++);
    }

    private char peekNext(){
        if(current + 1 >= length) return '\0';
        return source.charAt(current + 1);
    }


}