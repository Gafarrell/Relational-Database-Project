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

    private String tableName;
    private List<String> subargs = new ArrayList<>();


    public TableCreateCmd(ArrayList<String> args)
    {
        if (args.size() < 1) throw new RuntimeException("Invalid command parameters!");
        tableName = args.get(args.size()-1);
        if (args.size() > 1)
            subargs = args.subList(0, args.size()-1);
    }

    public boolean execute() throws Exception {
        if (!DatabaseConnector.getInstance().isUsing()){
            System.out.println("!Failed: No database currently being used.");
            return false;
        }
        ArrayList<SQLColumn> columns = new ArrayList<>();

        for (String s : subargs){
            String[] columnData = s.trim().split(" ");
            switch (columnData[1].toLowerCase()){
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
                    return false;
            }
        }

        if (DatabaseConnector.getInstance().getCurrent().addTable(tableName, columns)){
            System.out.println("Table " + tableName + " created.");
            return true;
        }
        throw new Exception("!Failed to create table " + tableName + " because it already exists.");
    }

    @Override
    public String getCommandString() {
        StringBuilder cmdStringBuilder = new StringBuilder();
        cmdStringBuilder.append("CREATE TABLE ").append(tableName).append(' ');
        for (String s : subargs){
            cmdStringBuilder.append(s).append(" ");
        }
        return cmdStringBuilder.toString();
    }

    public String getTableName(){return tableName;}
}
