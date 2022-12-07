package io.github.gafarrell.commands.modification;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;
import io.github.gafarrell.database.column.SQLColumn;

import java.util.Arrays;
import java.util.List;

public class DeleteCmd extends SQLCommand  {

    private final String table, where;

    public DeleteCmd(String table, String where){
        this.table = table.trim();
        this.where = where.trim();
    }

    @Override
    public boolean execute() throws Exception {
        Database current = DatabaseConnector.getInstance().getCurrent();
        Table targetTable = current.containsTable(table) ? current.getTable(table) : null;

        if (targetTable == null) {
            commandMessage = RED + "!Table " + table + " does not exist.";
            return false;
        }
        String[] whereTokens = where.split(" ");

        if (whereTokens.length == 3){
            commandMessage = RED + "!Where statement invalid.";
            return false;
        }

        SQLColumn column = targetTable.hasColumn(whereTokens[0]) ? targetTable.getColumn(whereTokens[0]) : null;

        if (column == null) {
            commandMessage = RED + "!Table " + table + " does not contain column " + whereTokens[0] + ".";
            return false;
        }

        List<Object> data = column.getData();

        switch (column.getType()){
            case STRING -> {

            }
            case FLOAT -> {}
            case INT -> {}
            default -> {}
        }

        return false;
    }
}
