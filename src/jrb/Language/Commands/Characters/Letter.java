package jrb.Language.Commands.Characters;

import jrb.Builder.Builder;

public class Letter extends Character {
    
    boolean uppercase = false;
    String from = null;
    String to = null;

    public Letter(boolean uppercase) {
        this.uppercase = uppercase;
        this.name = "letter";
    }

    public void setSpan(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        if (uppercase){
            if (from != null && to != null) {
                temp = temp.uppercaseLetter(from, to);
            } else {
                temp = temp.uppercaseLetter();
            }
        }else{
            if (from != null && to != null) {
                temp = temp.letter(from, to);
            } else {
                temp = temp.letter();
            }
        }
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }

}
