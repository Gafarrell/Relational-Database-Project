package io.github.gafarrell.commands.query;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

public class SelectCmd extends SQLCommand {

    private String tableName;

    public SelectCmd(String tableName){
        this.tableName = tableName;
    }

    @Override
    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().notUsingDB()){
            System.out.println("!Failed: No database currently being used.");
            return false;
        }

        System.out.println(DatabaseConnector.getInstance().getCurrent().selectAll(tableName));
        return false;
    }

    @Override
    public String getCommandString() {
        return "SELECT * FROM " + tableName;
    }
}
