package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.List;

public class DatabaseDropCmd extends SQLCommand {

    private String dbName;

    public DatabaseDropCmd(String dbName) throws Exception {
        this.dbName = dbName;
    }

    @Override
    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().dropDatabase(dbName)){
            commandMessage = GREEN + "Successfull dropped database " + dbName + ".";
            return true;
        }

        commandMessage = RED + "! Database " + dbName + " was unable to be dropped.";
        return false;
    }
}
