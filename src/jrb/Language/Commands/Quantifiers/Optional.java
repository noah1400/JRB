package jrb.Language.Commands.Quantifiers;

import jrb.Builder.Builder;

public class Optional extends Quantifier{

    public Optional() {
        this.name = "optional";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.optional();
    }
    
}
