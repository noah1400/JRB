package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class NoCharacter extends Character{

    public NoCharacter() {
        this.name = "noCharacter";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        temp = temp.noCharacter();
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
