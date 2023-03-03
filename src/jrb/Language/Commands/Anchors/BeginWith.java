package jrb.Language.Commands.Anchors;

import jrb.Builder.Builder;

public class BeginWith extends Anchor{

    public BeginWith() {
        this.name = "beginWith";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.startsWith();
    }
    
}
