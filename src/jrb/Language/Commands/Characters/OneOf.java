package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class OneOf extends Character {

    String literal;

    public OneOf(String literal) {
        this.literal = literal;
        this.name = "oneOf";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        temp = temp.oneOf(literal);
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
