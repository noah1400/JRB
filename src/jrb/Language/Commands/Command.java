package jrb.Language.Commands;

import jrb.Builder.Builder;
import jrb.Language.Commands.Quantifiers.Quantifier;

public abstract class Command {
    
    public String name;
    protected Quantifier quantifier;

    abstract public Builder callMethodOn(Builder builder);

    

    public void setQuantifier(Quantifier quantifier) {
        this.quantifier = quantifier;
    }

}
