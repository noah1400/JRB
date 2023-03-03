package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class Digit extends Character{

    String from = null;
    String to = null;

    public Digit() {
        this.name = "digit";
    }

    public void setSpan(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        if (from != null && to != null) {
            temp = temp.digit(from, to);
        } else {
            temp = temp.digit();
        }
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
