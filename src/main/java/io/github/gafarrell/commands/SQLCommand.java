package io.github.gafarrell.commands;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLCommand {
    protected List<String> parameters = new ArrayList<>();

    public abstract boolean execute() throws Exception;
    public abstract String getCommandString();
}
