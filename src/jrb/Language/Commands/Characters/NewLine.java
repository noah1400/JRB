package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class NewLine extends Character{

    public NewLine() {
        this.name = "newline";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        temp = temp.newLine();
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
