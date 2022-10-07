package io.github.gafarrell.commands.query;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.List;

public class SelectCmd extends SQLCommand {

    public SelectCmd(List<String> args){
        parameters = args;
    }

    @Override
    public boolean execute() throws Exception {
        if (parameters.get(0).equalsIgnoreCase("*")){
            DatabaseConnector.getInstance().getCurrent().select();
        }
        return false;
    }
}
