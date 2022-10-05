package io.github.gafarrell.commands;

import java.util.ArrayList;

public abstract class SQLCommand {
    protected ArrayList<String> parameters = new ArrayList<>();

    public SQLCommand(){}

    public abstract boolean execute();
}
