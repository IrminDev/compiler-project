package controller;

import model.Token;
import model.TokenType;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Lexer{
    private final String source;
    private final int length;
    private int current = 0;
    private int line = 1;

    private final Set<Character> singleCharTokens = Set.of
    ('(', ')', '{', '}', ',', '.', '-', '+', ';', '*', '/', '%', '!', '=', '<', '>', '&', '|', '^', '~');

    private final Set<Character> whiteSpaces = Set.of(' ', '\r', '\t', '\n'); 

    public Scanner(String source){
        this.source = source;
        this.length = source.length();
    }

    public List<Token> scanTokens(){
        List<Token> tokens = new ArrayList<>();

        while(!isAtEnd()){
            char c = peek();
            if(Character.isAlphabetic(c)){
                tokens.add(identifier());
            } else if(Character.isDigit(c)){
                token.add(number());
            } else if(c == '"'){
                tokens.add(string());
            } else if(whiteSpaces.contains(c)){
                skipWhiteSpaces();
            } else if(c == '/'){
                if(peekNext() == '/'){
                    skipComment();
                } else if(peekNext() == '*'){
                    skipComment();
                } else {
                    tokens.add(singleChar());
                }
            } else if(singleCharTokens.contains(c)){
                tokens.add(singleChar());
            } else if(c == '<' || c == '>' || c == '=' || c == '!'){
                tokens.add(relationalOperator());
            } else{
                throw new RuntimeException("Unexpected character: " + c);
            }
        }

        return tokens;
    }

    private Token identifier(){
        int start = current;
        while(Character.isAlphabetic(peekNext()) || Character.isDigit(peekNext())){
            advance();
        }
        String text = source.substring(start, current);
        TokenType type = identifierType(text);
        return new Token(type, text, line);
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

     /**
     * This method is used to handle the case of numbers
     * such as 123, 3.14, 0.123, 123.0 (check the transition diagram for more details)
     * @author Angel
     * @return Token
     */
    private Token number(){

    }

     /**
     * This method is used to handle the case of string literals
     * such as "Hello, World!" or "123" (check the transition diagram for more details)
     * @author Angel
     * @return Token
     */
    private Token string(){

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

     /**
     * This method is used to handle the case of white spaces
     * such as ' ', '\r', '\t', '\n'
     * @author Rodolfo
     * @return void
     */
    private void skipWhiteSpaces(){

    }

    private char advance(){
        return source.charAt(current++);
    }

    private char peekNext(){
        if(current + 1 >= length) return '\0';
        return source.charAt(current + 1);
    }

     /**
     * This method is used to handle the case of comments
     * such as //, /*, * / (check the transition diagram for more details)
     * @author Angel
     * @return Token
     */
    private void skipComment(){

    }

     /**
     * This method is used to handle the case of single character tokens
     * such as (, ), {, }, ,, ., -, +, ;, *, /, %, !, =, <, >, &, |, ^, ~
     * @author Rodolfo
     * @return Token
     */
    private Token singleChar(){

    }

    /**
     * This method is used to handle the case of relational operators
     * such as <, >, <=, >=, ==, !=
     * @author Rodolfo
     * @return Token
     */
    private Token relationalOperator(){

    }
}