package jrb.Language.Commands.Flags;

import jrb.Builder.Builder;

public class AllLazy extends Flag{

    public AllLazy() {
        this.name = "allLazy";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.allLazy();
    }
    
}
