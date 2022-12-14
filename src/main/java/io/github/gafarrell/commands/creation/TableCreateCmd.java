package io.github.gafarrell.commands.creation;

import io.github.gafarrell.Debug;
import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;
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

        Debug.writeLine(args.size());
    }

    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().notUsingDB()){
            commandMessage = "!Failed: No database currently being used.";
            return successful = false;
        }

        DatabaseConnector connector = DatabaseConnector.getInstance();
        Database current = connector.getCurrent();

        if (current.containsTable(tableName)){
            commandMessage = RED + "! Table " + tableName + " already exists.";
            return false;
        }

        ArrayList<SQLColumn> columns = new ArrayList<>();

        for (String s : args){
            String parsable = s.replaceAll("[()]", " ");
            String[] columnData = parsable.trim().split(" ");
            switch (columnData[1].toLowerCase().trim()){
                case "float":
                    columns.add(new FloatColumn(s));
                    Debug.writeLine("Adding float column");
                    break;
                case "int":
                    Debug.writeLine("Adding int column");
                    columns.add(new IntColumn(s));
                    break;
                case "char":
                case "varchar":
                    Debug.writeLine("Adding string column");
                    columns.add(new StringColumn(s, Integer.parseInt(columnData[2])));
                    break;
                default:
                    Debug.writeLine("Default...");
                    return successful = false;
            }
        }

        Table newTable = new Table(tableName, columns, current);

        if (connector.isTransactionActive()) connector.lockTable(newTable);
        else {
            newTable.save();
        }

        commandMessage = GREEN + "Created table " + tableName;
        return true;
    }
}
