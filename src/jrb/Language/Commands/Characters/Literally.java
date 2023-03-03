package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class Literally extends Character{

    String literal;

    public Literally(String literal) {
        this.literal = literal;
        this.name = "literally";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        temp = temp.literally(literal);
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
