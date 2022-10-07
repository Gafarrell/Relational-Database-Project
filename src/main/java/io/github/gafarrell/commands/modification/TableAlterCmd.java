package io.github.gafarrell.commands.modification;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.column.FloatColumn;
import io.github.gafarrell.database.column.IntColumn;
import io.github.gafarrell.database.column.SQLColumn;
import io.github.gafarrell.database.column.StringColumn;

import java.util.ArrayList;
import java.util.List;

public class TableAlterCmd extends SQLCommand {
    private String tableToEdit;

    public TableAlterCmd(List<String> params) throws Exception {
        this.tableToEdit = params.get(0);
        this.parameters = params.subList(1, params.size());
    }

    @Override
    public boolean execute() throws Exception {
        if (!DatabaseConnector.getInstance().isUsing()){
            System.out.println("!Failed: No database currently being used.");
            return false;
        }

        switch (parameters.get(0).toLowerCase()){
            case "drop":
                DatabaseConnector.getInstance().getCurrent().alterTableDrop(tableToEdit, parameters.subList(1, parameters.size()));
                break;

            case "add":
                ArrayList<SQLColumn> columns = new ArrayList<>();
                for (String param : parameters.subList(1, parameters.size())){
                    String[] columnData = param.toLowerCase().trim().split(" ");

                    switch (columnData[1].toLowerCase()){
                        case "float":
                            columns.add(new FloatColumn(param));
                            break;
                        case "int":
                            columns.add(new IntColumn(param));
                            break;
                        case "char":
                        case "varchar":
                            columns.add(new StringColumn(param, Integer.parseInt(columnData[2])));
                            break;
                        default:
                            return false;

                    }

                    if (DatabaseConnector.getInstance().getCurrent().alterTableAdd(tableToEdit, columns)){
                        System.out.println("Table " + tableToEdit + " modified.");
                    }
                    break;
                }
        }


        return false;
    }

    @Override
    public String getCommandString() {
        StringBuilder cmdCommandString = new StringBuilder();
        cmdCommandString.append("ALTER TABLE ").append(tableToEdit).append(' ');
        for (String s : parameters){
            cmdCommandString.append(s).append(' ');
        }
        return cmdCommandString.toString();
    }
}
