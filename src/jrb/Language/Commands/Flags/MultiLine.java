package jrb.Language.Commands.Flags;

import jrb.Builder.Builder;

public class MultiLine extends Flag{

    public MultiLine() {
        this.name = "multiLine";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.multiLine();
    }
    
}
