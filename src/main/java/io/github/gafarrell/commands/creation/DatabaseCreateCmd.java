package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.List;

public class DatabaseCreateCmd extends SQLCommand {

    private String dbName;

    public DatabaseCreateCmd(String dbName){
        this.dbName = dbName;
    }

    @Override
    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().createDatabase(dbName)){
            commandMessage = "Database " + dbName + " created.";
            return successful = true;
        }

        commandMessage = "!Failed to create database " + dbName + " because it already exists.";
        return successful = false;
    }
}
