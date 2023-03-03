package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class Tab extends Character{

    public Tab() {
        this.name = "tab";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        temp = temp.tab();
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
