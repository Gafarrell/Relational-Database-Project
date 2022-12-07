package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.column.FloatColumn;
import io.github.gafarrell.database.column.IntColumn;
import io.github.gafarrell.database.column.SQLColumn;
import io.github.gafarrell.database.column.StringColumn;

import java.util.ArrayList;
import java.util.List;

public class TableCreateCmd extends SQLCommand {

    private final String tableName;
    private final List<String> args;

    public TableCreateCmd(String tableName , List<String> args)
    {
        this.tableName = tableName;
        this.args = args;
    }

    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().notUsingDB()){
            commandMessage = "!Failed: No database currently being used.";
            return successful = false;
        }

        ArrayList<SQLColumn> columns = new ArrayList<>();

        for (String s : args){
            String parsable = s.replaceAll("[()]", " ");
            String[] columnData = parsable.trim().split(" ");
            switch (columnData[1].toLowerCase().trim()){
                case "float":
                    columns.add(new FloatColumn(s));
                    break;
                case "int":
                    columns.add(new IntColumn(s));
                    break;
                case "char":
                case "varchar":
                    columns.add(new StringColumn(s, Integer.parseInt(columnData[2])));
                    break;
                default:
                    return successful = false;
            }
        }

        if (DatabaseConnector.getInstance().getCurrent().addTable(tableName, columns)){
            commandMessage = "Table " + tableName + " created.";
            return successful = true;
        }

        commandMessage = "!Failed to create table " + tableName + " because it already exists.";
        return successful = false;
    }
}
