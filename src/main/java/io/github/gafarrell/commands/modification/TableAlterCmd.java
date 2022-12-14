package io.github.gafarrell.commands.modification;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;
import io.github.gafarrell.database.column.FloatColumn;
import io.github.gafarrell.database.column.IntColumn;
import io.github.gafarrell.database.column.SQLColumn;
import io.github.gafarrell.database.column.StringColumn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class TableAlterCmd extends SQLCommand {
    private final String tableToEdit;
    private final List<String> parameters;

    public TableAlterCmd(String tableName, List<String> alterParams) throws Exception {
        this.tableToEdit = tableName;
        this.parameters = alterParams;
    }

    @Override
    public boolean execute() throws Exception {
        DatabaseConnector connector = DatabaseConnector.getInstance();

        if (connector.notUsingDB()){
            commandMessage = "!Failed: No database currently being used.";
            return false;
        }

        Database current = DatabaseConnector.getInstance().getCurrent();

        if (!current.containsTable(tableToEdit)) {
            commandMessage = RED + "! Table " + tableToEdit + " does not exist.";
            return false;
        }

        Table table = current.getTable(tableToEdit);


        switch (parameters.get(0).toLowerCase()){
            case "drop" -> {
                return executeDrop(table);
            }
            case "add" -> {
                return executeAdd(table);
            }
            default ->{
                commandMessage = RED + "! Not a valid alter command";
                return false;
            }
        }
    }


    private boolean executeAdd(Table table) throws Exception {

        List<SQLColumn> currentColumns = table.getColumns();
        String[] newColumns = parameters.get(1).split(",");

        for (String newColumn : newColumns){
            String title = newColumn.split(" ")[0];
            String type = newColumn.split(" ")[1];

            SQLColumn column;

            if (type.contains("varchar(")) {
                int size = Integer.parseInt(type.substring(8, type.length()-1));
                column = new StringColumn(title, size);
            }
            else {
                switch (type) {
                    case "int" -> {
                        column = new IntColumn(title);
                    }
                    case "float" -> {
                        column = new FloatColumn(title);
                    }
                    default -> {
                        commandMessage = "! Invalid column type: " + type;
                        return false;
                    }
                }
            }

            currentColumns.add(column);
        }
        table.setColumns(currentColumns);
        DatabaseConnector.getInstance().lockTable(table);
        commandMessage = GREEN + "1 New Record Inserted";
        return true;
    }

    private boolean executeDrop(Table table) throws Exception {

        List<SQLColumn> columns = table.getColumns();
        String[] targetColumns = parameters.get(1).split(",");

        for (String title : targetColumns){
            Iterator<SQLColumn> sqlColumnIterator = columns.iterator();

            while (sqlColumnIterator.hasNext()){
                SQLColumn col = sqlColumnIterator.next();

                if (col.getTitle().equals(title)){
                    columns.remove(col);
                    continue;
                }
                commandMessage = RED + "! Table " + tableToEdit + " does not contain column " + title;
                return false;
            }
        }

        table.setColumns(columns);
        DatabaseConnector.getInstance().lockTable(table);
        commandMessage = GREEN + "Removed " + targetColumns.length + " column(s).";
        return true;
    }
}
