package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class Whitespace extends Character{

    boolean no = false;

    public Whitespace(boolean no) {
        this.no = no;
        this.name = "whitespace";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        if (no) {
            temp = temp.noWhitespace();
        } else {
            temp = temp.whitespace();
        }
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
