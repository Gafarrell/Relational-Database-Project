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
            System.out.println("Database " + dbName + " created.");
            return true;
        }
        else
            System.out.println("!Failed to create database " + dbName + " because it already exists.");

        return false;
    }

    @Override
    public String getCommandString() {
        return "CREATE DATABASE " + dbName;
    }
}
