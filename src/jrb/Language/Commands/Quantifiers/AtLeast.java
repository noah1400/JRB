package jrb.Language.Commands.Quantifiers;

import jrb.Builder.Builder;

public class AtLeast extends Quantifier{

    private int n;

    public AtLeast(int n) {
        this.n = n;
        this.name = "atLeast";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.atLeast(this.n);
    }
    
}
