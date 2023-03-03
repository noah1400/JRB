package jrb.Language.Commands.Anchors;

import jrb.Builder.Builder;

public class MustEnd extends Anchor{

    public MustEnd() {
        this.name = "mustEnd";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        return builder.mustEnd();
    }
    
}
