package jrb.Language.Commands.Lookarounds;

import java.util.ArrayList;

import jrb.Builder.Builder;
import jrb.Language.Commands.Command;

public class IfFollowedBy extends Lookaround{

    public IfFollowedBy(String literal, boolean positive) {
        super(positive);
        this.literal = literal;
        this.name = positive ? "ifFollowedBy" : "ifNotFollowedBy";
    }

    public IfFollowedBy(ArrayList<Command> condition, boolean positive) {
        super(positive);
        this.condition = condition;
        this.name = positive ? "ifFollowedBy" : "ifNotFollowedBy";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        if (this.condition.size() > 0) {
            Builder group = new Builder();
            for (Command command : this.condition) {
                group = command.callMethodOn(group);
            }
            temp = positive ? temp.ifFollowedBy(group) : temp.ifNotFollowedBy(group);
        } else {
            temp = positive ? temp.ifFollowedBy(literal) : temp.ifNotFollowedBy(literal);
        }
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
}
