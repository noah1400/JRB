package jrb.Language.Commands.Quantifiers;

import jrb.Builder.Builder;

public class Exactly extends Quantifier{

    private int count;

    public Exactly(int count) {
        this.count = count;
        this.name = "exactly";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.exactly(count);
    }
    
}
