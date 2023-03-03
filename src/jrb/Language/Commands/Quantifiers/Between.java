package jrb.Language.Commands.Quantifiers;

import jrb.Builder.Builder;

public class Between extends Quantifier{

    private int min;
    private int max;

    public Between(int min, int max) {
        this.min = min;
        this.max = max;
        this.name = "between";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.between(min, max);
    }
    
}
