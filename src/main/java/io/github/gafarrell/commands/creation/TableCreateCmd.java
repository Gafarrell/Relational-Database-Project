package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.column.FloatColumn;
import io.github.gafarrell.database.column.IntColumn;
import io.github.gafarrell.database.column.SQLColumn;
import io.github.gafarrell.database.column.StringColumn;
import io.github.gafarrell.database.table.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableCreateCmd extends SQLCommand {

    private Database database;
    private String tableName;
    private List<String> subargs;


    public TableCreateCmd(Database db, ArrayList<String> args)
    {
        if (args.size() < 1) throw new RuntimeException("Invalid command parameters!");
        tableName = args.get(0);
        subargs = args.subList(1, args.size());
        this.database = db;
    }

    public boolean execute(){
        ArrayList<SQLColumn> columns = new ArrayList<>();

        for (String s : subargs){
            String[] columnData = s.split(" ");

            switch (columnData[1].toLowerCase()){
                case "float":
                    columns.add(new FloatColumn(columnData[0]));
                    break;
                case "int":
                    columns.add(new IntColumn(columnData[0]));
                    break;
                case "varchar":
                    columns.add(new StringColumn(columnData[0], Integer.parseInt(columnData[2])));
                    break;
                default:
                    return false;
            }
        }

        Table table = new Table(tableName, columns);
        database.createTable(table);
        return true;
    }

    public String getTableName(){return tableName;}
}
