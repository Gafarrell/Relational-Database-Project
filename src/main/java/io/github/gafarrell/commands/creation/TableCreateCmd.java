package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.column.FloatColumn;
import io.github.gafarrell.database.column.IntColumn;
import io.github.gafarrell.database.column.SQLColumn;
import io.github.gafarrell.database.column.StringColumn;
import io.github.gafarrell.database.Table;

import java.util.ArrayList;
import java.util.List;

public class TableCreateCmd extends SQLCommand {

    private String tableName;
    private List<String> subargs;


    public TableCreateCmd(ArrayList<String> args)
    {
        if (args.size() < 1) throw new RuntimeException("Invalid command parameters!");
        tableName = args.get(0);
        subargs = args.subList(1, args.size());
    }

    public boolean execute() throws Exception {
        System.out.println("Executing table creation...");
        ArrayList<SQLColumn> columns = new ArrayList<>();

        if (subargs.size() != 0) System.out.println("Adding columns...");
        for (String s : subargs){
            String[] columnData = s.split(" ");
            switch (columnData[1].toLowerCase()){
                case "float":
                    columns.add(new FloatColumn(columnData[0]));
                    System.out.println("Adding float column...");
                    break;
                case "int":
                    columns.add(new IntColumn(columnData[0]));
                    System.out.println("Adding integer column...");
                    break;
                case "varchar":
                    columns.add(new StringColumn(columnData[0], Integer.parseInt(columnData[2])));
                    System.out.println("Adding string column...");
                    break;
                default:
                    return false;
            }
        }
        System.out.println("Done adding columns...");
        Table table = new Table(tableName, columns, DatabaseConnector.getInstance().getCurrent());
        System.out.println("Exited table constructor");
        return DatabaseConnector.getInstance().getCurrent().createTable(table);
    }

    public String getTableName(){return tableName;}
}
