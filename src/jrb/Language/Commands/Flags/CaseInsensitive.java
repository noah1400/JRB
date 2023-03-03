package jrb.Language.Commands.Flags;

import jrb.Builder.Builder;

public class CaseInsensitive extends Flag{

    public CaseInsensitive() {
        this.name = "caseInsensitive";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.caseInsensitive();
    }
    
}
