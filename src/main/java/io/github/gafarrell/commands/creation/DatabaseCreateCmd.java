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
            System.out.println("Database " + parameters.get(0) + " created.");
            return true;
        }
        else
            System.out.println("!Failed to create database " + parameters.get(0) + " because it already exists.");

        return false;
    }

    @Override
    public String getCommandString() {
        return "CREATE DATABASE " + parameters.get(0);
    }
}
