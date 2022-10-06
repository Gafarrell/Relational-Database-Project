package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.List;

public class DatabaseCreateCmd extends SQLCommand {

    public DatabaseCreateCmd(List<String> parameters){
        this.parameters = parameters;
    }

    @Override
    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().createDatabase(parameters.get(0))){
            System.out.println("Successfully created database " + parameters.get(0));
        }
        else
            System.out.println("!Failed Unable to create database!");

        return false;
    }
}
