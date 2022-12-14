package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;

import java.util.List;

public class TableDropCmd extends SQLCommand {

    private final String tableName;

    public TableDropCmd(String tableName){
        this.tableName = tableName;
    }

    @Override
    public boolean execute() throws Exception {
        DatabaseConnector connector = DatabaseConnector.getInstance();
        Database current = connector.getCurrent();

        if (!current.containsTable(tableName)){
            commandMessage = RED + "! Table " + tableName + " does not exist in the current database.";
            return false;
        }

        Table table = current.getTable(tableName);
        if (connector.isTableLocked(table)){
            commandMessage = RED + "! Table " + tableName + " is locked.";
            return false;
        }

        if (connector.isTransactionActive()) connector.lockTable(table);
        else if (current.getTables().remove(tableName) != null){
            table.delete();
        }

        commandMessage = GREEN + "Table " + tableName + " successfully removed.";
        return true;
    }
}
