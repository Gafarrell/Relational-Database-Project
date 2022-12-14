package io.github.gafarrell.commands.modification;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;
import io.github.gafarrell.database.column.SQLColumn;

import java.util.List;

public class InsertCmd extends SQLCommand {

    private String tableName;
    private List<String> parameters;

    public InsertCmd(String tableName, List<String> values) {
        this.tableName = tableName;
        this.parameters = values;
    }

    @Override
    public boolean execute() throws Exception {
        // Database Connector and guarding statements.
        DatabaseConnector connector = DatabaseConnector.getInstance();

        if (connector.notUsingDB()) {
            commandMessage = RED + "! No database in use";
            return false;
        }

        // Database and guarding statements.
        Database current = DatabaseConnector.getInstance().getCurrent();

        if (!current.containsTable(tableName)){
            commandMessage = RED + "! Table " + tableName + " not found.";
            return false;
        }

        // Table and guarding statements
        Table target = current.getTable(tableName);

        if (connector.isTableLocked(target)){
            commandMessage = RED + "! Table " + tableName + " is locked.";
            return false;
        }

        // Target columns and guarding statements.
        List<SQLColumn> targetColumns = target.getColumns();

        if (targetColumns.isEmpty()){
            commandMessage = RED + "! Table " + tableName + " contains no columns";
            return false;
        }

        if (parameters.size() != targetColumns.size()){
            commandMessage = RED + "! Invalid number of parameters";
            return false;
        }

        // Main algorithm.
        for (int i = 0; i < targetColumns.size(); i++){
            if (!targetColumns.get(i).queueData(parameters.get(i))){
                commandMessage = RED + "!Invalid parameter type for column " + targetColumns.get(i).getTitle();
                for (SQLColumn c : targetColumns) c.clearQueue();
            }
        }

        for (SQLColumn c : targetColumns) c.insertQueue();

        // Closing actions.
        if (connector.isTransactionActive()) connector.lockTable(target);
        else{
            target.save();
        }

        commandMessage = GREEN + "Added entry";
        return true;
    }
}
