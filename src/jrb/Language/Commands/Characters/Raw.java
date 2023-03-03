package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;
import jrb.Exceptions.BuilderException;

public class Raw extends Character{
    
    String raw;

    public Raw(String raw) {
        this.raw = raw;
        this.name = "raw";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        try {
            temp = temp.raw(name);
        } catch (BuilderException e) {
            e.printStackTrace();
        }
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
}
