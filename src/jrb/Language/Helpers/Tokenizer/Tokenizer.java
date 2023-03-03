package jrb.Language.Helpers.Tokenizer;

import java.util.ArrayList;

public class Tokenizer {

    protected String raw = "";
    protected int pointer;
    protected ArrayList<Token> tokens = new ArrayList<Token>();
    
    public Tokenizer(String raw) {
        this.raw = raw;
    }

    public ArrayList<Token> getTokens() {
        if (this.tokens.size() == 0) {
            this.tokenize();
        }
        return this.tokens;
    }

    public ArrayList<Token> tokenize() {
        this.pointer = 0;
        while (this.pointer < this.raw.length()) {
            if (this.isLetter(this.current())) {
                this.tokens.add(this.word());
            } else if (this.current() == '"' || this.current() == '\'') {
                this.tokens.add(this.string());
            } else if (this.current() == '(') {
                this.tokens.add(new Token("(", Token.T_LPAREN, this.pointer));
                this.next();
            } else if (this.current() == ')') {
                this.tokens.add(new Token(")", Token.T_RPAREN, this.pointer));
                this.next();
            } else if (this.isNumber(this.current())) {
                this.tokens.add(this.number());
            } else {
                this.next();
            }
        }

        return this.tokens;
    }

    private void next() {
        this.pointer++;
    }

    private void next(int amount) {
        this.pointer += amount;
    }

    private char current() {

        if (this.pointer >= this.raw.length()) {
            return '\0';
        }

        return this.raw.charAt(this.pointer);
    }

    private char peak() {

        if (this.pointer + 1 >= this.raw.length()) {
            return '\0';
        }

        return this.raw.charAt(this.pointer + 1);
    }

    private char peak(int amount) {

        if (this.pointer + amount >= this.raw.length()) {
            return '\0';
        }

        return this.raw.charAt(this.pointer + amount);
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private boolean isNumber(char c) {
        return Character.isDigit(c);
    }

    private Token word() {
        String word = "";
        int pos = this.pointer;
        while (this.isLetter(this.current()) || this.isNumber(this.current())) {
            word += this.current();
            this.next();
        }
        return new Token(word, Token.T_WORD, pos);
    }

    private Token string() {
        char quote = this.current();
        int pos = this.pointer;
        this.next();
        String string = "";
        while (this.current() != quote) {
            string += this.current();
            this.next();
        }
        this.next();
        return new Token(string, Token.T_STRING, pos);
    }

    private Token number() {
        String number = "";
        int pos = this.pointer;
        while (this.isNumber(this.current())) {
            number += this.current();
            this.next();
        }
        return new Token(number, Token.T_NUMBER, pos);
    }

}
