package io.github.gafarrell.commands.modification;

import io.github.gafarrell.Debug;
import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;
import io.github.gafarrell.database.column.SQLColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteCmd extends SQLCommand  {

    private final String table, where;

    public DeleteCmd(String table, String where){
        this.table = table.trim();
        this.where = where.trim();

        Debug.writeLine("Table: " + table);
        Debug.writeLine("Where: " + where);
    }

    @Override
    public boolean execute() throws Exception {
        DatabaseConnector connector = DatabaseConnector.getInstance();
        Database current = connector.getCurrent();
        Table targetTable = current.containsTable(table) ? current.getTable(table) : null;

        if (targetTable == null) {
            commandMessage = RED + "!Table " + table + " does not exist.";
            return false;
        }
        if (connector.isTableLocked(targetTable)){
            commandMessage = RED + "! Table " + table + " is locked.";
            return false;
        }

        String[] whereTokens = where.split(" ");

        if (whereTokens.length != 3){
            commandMessage = RED + "!Where statement invalid.";
            return false;
        }

        SQLColumn column = targetTable.hasColumn(whereTokens[0]) ? targetTable.getColumn(whereTokens[0]) : null;

        if (column == null) {
            commandMessage = RED + "!Table " + table + " does not contain column " + whereTokens[0] + ".";
            return false;
        }

        List<Object> data = column.getData();
        int dataRemoved = 0;

        for (int i = 0; i < data.size(); i++){
            switch (column.getType()){
                case INT -> {
                    int w = Integer.parseInt(whereTokens[2]);
                    if (compare(data.get(i), w, whereTokens[1])) {
                        column.deleteDataAt(i);
                        dataRemoved++;
                    }
                }
                case FLOAT -> {
                    float w = Float.parseFloat(whereTokens[2]);
                    if (compare(data.get(i), w, whereTokens[1])){
                        column.deleteDataAt(i);
                        dataRemoved++;
                    }
                }
                case STRING -> {
                    if (compare(data.get(i), whereTokens[2], whereTokens[1])){
                        column.deleteDataAt(i);
                        dataRemoved++;
                    }
                }
                default -> {
                    commandMessage = RED + "! Invalid SQL Column type detected.";
                    return false;
                }
            }
        }

        if (connector.isTransactionActive()) connector.lockTable(targetTable);
        else targetTable.save();

        commandMessage = GREEN + "Successfully removed " + dataRemoved + " entries";
        return true;
    }
}
