package jrb.Language.Commands.Quantifiers;

import jrb.Builder.Builder;

public class OnceOrMore extends Quantifier{

    public OnceOrMore() {
        this.name = "onceOrMore";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.onceOrMore();
    }
    
}
