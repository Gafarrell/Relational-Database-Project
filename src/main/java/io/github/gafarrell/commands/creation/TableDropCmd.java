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

        if (DatabaseConnector.getInstance().notUsingDB()){
            System.out.println("!Failed: No database currently being used.");
            return false;
        }

        if (DatabaseConnector.getInstance().getCurrent().dropTable(dbName)){
            System.out.println("Table " + dbName + " deleted.");
            return true;
        }
        return false;
    }

    @Override
    public String getCommandString() {
        return "DROP TABLE " + parameters.get(0);
    }
}
