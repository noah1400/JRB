package jrb.Language.Commands.Quantifiers;

import jrb.Builder.Builder;

public class NeverOrMore extends Quantifier{

    public NeverOrMore() {
        this.name = "neverOrMore";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.neverOrMore();
    }
    
}
