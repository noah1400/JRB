package jrb.Language.Commands.Groups;

import java.util.ArrayList;

import jrb.Builder.Builder;
import jrb.Language.Commands.Command;

public class CaptureAs extends Group{

    private String as;

    public CaptureAs(ArrayList<Command> condition, String as) {
        this.condition = condition;
        this.as = as;
        this.name = "captureAs";
    }

    public CaptureAs(ArrayList<Command> condition) {
        this.condition = condition;
        this.name = "capture";
    }

    @Override
    public Builder callMethodOn(Builder builder) {
        Builder temp = builder;
        Builder group = new Builder();
        for (Command command : condition) {
            group = command.callMethodOn(group);
        }
        if (as != null) {
            temp = temp.capture(group, as);
        } else {
            temp = temp.capture(group);
        }
        if (quantifier != null) {
            temp = quantifier.callMethodOn(temp);
        }
        return temp;
    }
    
}
