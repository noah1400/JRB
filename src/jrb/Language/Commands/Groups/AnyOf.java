package jrb.Language.Commands.Groups;

import java.util.ArrayList;

import jrb.Builder.Builder;
import jrb.Language.Commands.Command;

public class AnyOf extends Group{

    public AnyOf(ArrayList<Command> condition) {
        this.condition = condition;
        this.name = "anyOf";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        Builder group = new Builder();
        for (Command command : condition) {
            group = command.callMethodOn(group);
        }
        temp = temp.anyOf(group);
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
