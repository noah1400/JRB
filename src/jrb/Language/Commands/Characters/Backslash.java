package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class Backslash extends Character{

    public Backslash() {
        this.name = "backslash";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        temp = temp.backslash();
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
