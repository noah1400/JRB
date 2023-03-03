package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class Anything extends Character{

    public Anything() {
        this.name = "anything";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        temp = temp.any();
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
