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
        if (!DatabaseConnector.getInstance().isUsing()){
            System.out.println("!Failed: No database currently being used.");
            return false;
        }
        if (parameters.get(0).equalsIgnoreCase("*")){
            System.out.println(DatabaseConnector.getInstance().getCurrent().selectAll(parameters.get(1)));
        }
        return false;
    }

    @Override
    public String getCommandString() {
        return "SELECT * FROM " + parameters.get(1);
    }
}
