package io.github.gafarrell.commands.query;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.ArrayList;
import java.util.List;

public class UseCmd extends SQLCommand {
    private String dbName;

    public UseCmd(String dbName){
        this.dbName = dbName;
    }

    @Override
    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().use(dbName)){
            commandMessage = GREEN + "Using database " + dbName + ".";
            return true;
        }

        commandMessage = RED + "! No database exists with name " + dbName + ".";
        return false;
    }
}
