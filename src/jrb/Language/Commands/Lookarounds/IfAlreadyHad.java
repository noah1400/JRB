package jrb.Language.Commands.Lookarounds;

import java.util.ArrayList;

import jrb.Builder.Builder;
import jrb.Language.Commands.Command;

public class IfAlreadyHad extends Lookaround{

    public IfAlreadyHad(String literal, boolean positive) {
        super(positive);
        this.literal = literal;
        this.name = positive ? "ifAlreadyHad" : "ifNotAlreadyHad";
    }

    public IfAlreadyHad(ArrayList<Command> condition, boolean positive) {
        super(positive);
        this.condition = condition;
        this.name = positive ? "ifAlreadyHad" : "ifNotAlreadyHad";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        if (this.condition.size() > 0) {
            Builder group = new Builder();
            for (Command command : this.condition) {
                group = command.callMethodOn(group);
            }
            temp = positive ? temp.ifAlreadyHad(group) : temp.ifNotAlreadyHad(group);
        } else {
            temp = positive ? temp.ifAlreadyHad(literal) : temp.ifNotAlreadyHad(literal);
        }
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
