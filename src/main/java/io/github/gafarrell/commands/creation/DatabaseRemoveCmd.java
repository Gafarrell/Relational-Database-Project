package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.List;

public class DatabaseRemoveCmd extends SQLCommand {

    public DatabaseRemoveCmd(List<String> args) throws Exception {
        this.parameters = args;
    }

    @Override
    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().dropDatabase(parameters.get(0))){
            System.out.println("Successfull dropped database " + parameters.get(0) + ".");
            return true;
        }
        else
            System.out.println("Failed! Database " + parameters.get(0) + " was unable to be dropped.");

        return false;
    }
}
