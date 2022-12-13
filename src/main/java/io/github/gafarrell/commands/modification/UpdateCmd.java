package io.github.gafarrell.commands.modification;

import io.github.gafarrell.Debug;
import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;
import io.github.gafarrell.database.column.SQLColumn;

import java.util.List;

public class UpdateCmd extends SQLCommand {
    private final String table, set, where;

    public UpdateCmd(String table, String set, String where){
        this.table = table.trim();
        this.set = set.trim();
        this.where = where.trim();
    }

    @Override
    public boolean execute() throws Exception {

        if (set.split(" ").length != 3 || where.split(" ").length != 3){
            commandMessage = RED + "! Invalid set/where parameters.";
            return false;
        }

        if (DatabaseConnector.getInstance().notUsingDB()){
            commandMessage = RED + "! No database in use.";
            return false;
        }

        Database current = DatabaseConnector.getInstance().getCurrent();

        if (!current.containsTable(table)){
            commandMessage = RED + "! Database " + current.getDbName() + " does not contain a table named " + table;
            return false;
        }

        Table currentTable = current.getTable(table);
        String[] setTokens = set.split(" ");
        String[] whereTokens = where.split(" ");

        if (!currentTable.hasColumn(setTokens[0])){
            commandMessage = RED + "! Table " + table + " does not contain column " + set;
            return false;
        }
        if (!currentTable.containsColumn(whereTokens[0])){
            commandMessage = RED + "! Table " + table + " does not contain column " + whereTokens[0];
            return false;
        }

        SQLColumn setColumn = currentTable.getColumn(setTokens[0]),
                whereColumn = currentTable.getColumn(whereTokens[0]);

        int dataCount = 0;
        for (int i = 0; i < whereColumn.getColumnSize(); i++){
            if (compare(whereColumn.getDataAtRow(i), whereTokens[2], whereTokens[1])){
                setColumn.setDataAtRow(i, setTokens[2]);
                dataCount++;
            }
        }

        if (dataCount == 0){
            commandMessage = YELLOW + "! No data changed.";
            return true;
        }

        commandMessage = GREEN + dataCount + " entries modified.";
        return true;
    }
}
