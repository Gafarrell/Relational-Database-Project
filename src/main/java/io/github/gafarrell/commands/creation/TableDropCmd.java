package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.List;

public class TableDropCmd extends SQLCommand {

    private String dbName;

    public TableDropCmd(String dbName){
        this.dbName = dbName;
    }

    @Override
    public boolean execute() throws Exception {

        try {
            if (DatabaseConnector.getInstance().notUsingDB()) {
                commandMessage = RED + "! No database currently being used.";
                return successful = false;
            }

            if (DatabaseConnector.getInstance().getCurrent().dropTable(dbName)) {
                commandMessage = GREEN + "Table " + dbName + " deleted.";
                return successful = true;
            }

            commandMessage = RED + "! Unable to drop database " + dbName;
            return successful = false;
        }
        catch (Exception e){
            commandMessage = e.getMessage();
            return successful = false;
        }
    }
}
