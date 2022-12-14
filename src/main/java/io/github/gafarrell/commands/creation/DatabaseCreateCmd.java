package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.HashMap;

public class DatabaseCreateCmd extends SQLCommand {

    private final String dbName;

    public DatabaseCreateCmd(String dbName){
        this.dbName = dbName;
    }

    @Override
    public boolean execute() throws Exception {
        DatabaseConnector connector = DatabaseConnector.getInstance();
        HashMap<String, Database> activeDatabases = connector.getActiveDatabases();

        if (activeDatabases.containsKey(dbName)){
            commandMessage = RED + "!Failed to create database " + dbName + " because it already exists.";
            return successful = false;
        }

        Database db = new Database(dbName);

        activeDatabases.putIfAbsent(dbName, db);

        connector.setActiveDatabases(activeDatabases);
        commandMessage = GREEN + "Database " + dbName + " created.";
        return true;
    }
}