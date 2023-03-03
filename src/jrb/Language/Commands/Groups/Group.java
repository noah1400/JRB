package jrb.Language.Commands.Groups;

import java.util.ArrayList;

import jrb.Language.Commands.Command;

public abstract class Group extends Command{
    ArrayList<Command> condition = new ArrayList<Command>();
}
