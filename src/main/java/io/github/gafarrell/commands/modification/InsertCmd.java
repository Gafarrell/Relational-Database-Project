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
        if (DatabaseConnector.getInstance().notUsingDB()) {
            commandMessage = RED + "! No database in use";
            return false;
        }

        Database current = DatabaseConnector.getInstance().getCurrent();

        if (!current.containsTable(tableName)){
            commandMessage = RED + "! Table " + tableName + " not found.";
            return false;
        }

        Table target = current.getTable(tableName);
        List<SQLColumn> targetColumns = target.getColumns();

        if (targetColumns.isEmpty()){
            commandMessage = RED + "! Table " + tableName + " contains no columns";
            return false;
        }

        if (parameters.size() != targetColumns.size()){
            commandMessage = RED + "! Invalid number of parameters";
            return false;
        }

        for (int i = 0; i < targetColumns.size(); i++){
            if (!targetColumns.get(i).queueData(parameters.get(i))){
                commandMessage = RED + "!Invalid parameter type for column " + targetColumns.get(i).getTitle();
                for (SQLColumn c : targetColumns) c.clearQueue();
            }
        }

        for (SQLColumn c : targetColumns) c.insertQueue();
        commandMessage = GREEN + "Added entry";
        return true;
    }
}
