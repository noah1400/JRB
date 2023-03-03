package jrb.Language.Commands.Groups;

import java.util.ArrayList;

import jrb.Builder.Builder;
import jrb.Language.Commands.Command;

public class Until extends Group{

    public Until(ArrayList<Command> condition) {
        this.condition = condition;
        this.name = "until";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        Builder group = new Builder();
        for (Command command : condition) {
            group = command.callMethodOn(group);
        }
        temp = temp.until(group);
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
