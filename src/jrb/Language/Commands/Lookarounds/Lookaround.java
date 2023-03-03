package jrb.Language.Commands.Lookarounds;

import java.util.ArrayList;

import jrb.Language.Commands.Command;

public abstract class Lookaround extends Command{
    ArrayList<Command> condition = new ArrayList<Command>(); // if condition is a group
    String literal = ""; // if condition is a literal
    
    protected boolean positive; // positive or negative lookaround

    public Lookaround(boolean positive) {
        this.positive = positive;
    }
}
