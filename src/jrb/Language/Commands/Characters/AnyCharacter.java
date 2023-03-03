package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class AnyCharacter extends Character{

    public AnyCharacter() {
        this.name = "anyCharacter";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        temp = temp.anyCharacter();
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
