package com.github.irmindev.model;

/**
 * This class represents a token in the language.
 * The token base class is abstract and has four subclasses:
 * - ValueToken: Represents a token with a literal value and it's not obvious the literal value (numbers and strings).
 * - Indetifier: Represents a token that is an identifier.
 * - KeywordToken: Represents a token that is a keyword.
 * - KnownValueToken: Represents a token with a literal value and it's obvious the literal value (true, false, null).
 * - EOFToken: Represents the end of the file.
 * 
 * @author Irmin
 * @version 1.0
 */
public sealed abstract class Token permits 
    Token.ValueToken,
    Token.Indetifier,
    Token.KeywordToken,
    Token.KnownValueToken,
    Token.EOFToken
{
    private final TokenType type;

    public Token(TokenType type) {
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    public static final class ValueToken extends Token {
        private final Object literal;
        private final int line;
        private final String lexeme;

        public ValueToken(TokenType type, Object literal, int line, String lexeme) {
            super(type);
            this.literal = literal;
            this.line = line;
            this.lexeme = lexeme;
        }

        public int getLine() {
            return line;
        }

        public Object getLiteral() {
            return literal;
        }

        public String getLexeme() {
            return lexeme;
        }
    }

    public static final class Indetifier extends Token {
        private final String lexeme;
        private final int line;

        public Indetifier(TokenType type, String lexeme, int line) {
            super(type);
            this.lexeme = lexeme;
            this.line = line;
        }

        public String getLexeme() {
            return lexeme;
        }

        public int getLine() {
            return line;
        }
    }

    public static final class KeywordToken extends Token {
        private final int line;

        public KeywordToken(TokenType type, int line) {
            super(type);
            this.line = line;
        }

        public int getLine() {
            return line;
        }
    }

    public static final class KnownValueToken extends Token {
        private final int line;
        private final Object literal;

        public KnownValueToken(TokenType type, int line, Object literal) {
            super(type);
            this.line = line;
            this.literal = literal;
        }

        public int getLine() {
            return line;
        }

        public Object getLiteral() {
            return literal;
        }
    }

    public static final class EOFToken extends Token {
        private final String lexeme;

        public EOFToken(TokenType type, String lexeme) {
            super(type);
            this.lexeme = lexeme;
        }

        public String getLexeme() {
            return lexeme;
        }
    }

    /**
     * This method prints the token with the following format:
     * <TYPE, LEXEME, LITERAL, LINE>
     * If one of the fields is null, it will not be printed.
     * 
     * @author Irmin
     * @param token The token to be printed.
     * @return void
     */
    public static void printToken(Token token) {
        System.out.print("<" + token.getType());
        if (token instanceof ValueToken valueToken) {
            System.out.print(", " + valueToken.getLexeme() + ", " + valueToken.getLiteral() + ", " + valueToken.getLine());
        } else if (token instanceof Indetifier identifier) {
            System.out.print(", " + identifier.getLexeme() + ", " + identifier.getLine());
        } else if (token instanceof KeywordToken keywordToken) {
            System.out.print(", " + keywordToken.getLine());
        } else if (token instanceof KnownValueToken knownValueToken) {
            System.out.print(", " + knownValueToken.getLiteral() + ", " + knownValueToken.getLine());
        } else if (token instanceof EOFToken eofToken) {
            System.out.print(", " + eofToken.getLexeme());
        }
        System.out.println(">");
    }
}