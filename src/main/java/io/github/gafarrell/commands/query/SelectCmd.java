package io.github.gafarrell.commands.query;

import io.github.gafarrell.Debug;
import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;
import io.github.gafarrell.database.column.FloatColumn;
import io.github.gafarrell.database.column.IntColumn;
import io.github.gafarrell.database.column.SQLColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCmd extends SQLCommand {

    private enum SubCommand{
        SELECT_MAX(Pattern.compile("max\\((.+?)\\)", Pattern.CASE_INSENSITIVE)),
        SELECT_AVG(Pattern.compile("avg\\((.+?)\\)", Pattern.CASE_INSENSITIVE)),
        SELECT_COUNT(Pattern.compile("count\\((.+?)\\)", Pattern.CASE_INSENSITIVE));

        public final Pattern commandPattern;
        SubCommand(Pattern cp){this.commandPattern = cp;}
    }

    private String tableName, condition, columns;
    private SubCommand subSelect = null;

    public SelectCmd(String tableName, String columns, String condition){
        this.tableName = tableName.trim();
        this.condition = condition == null ? "" : condition.trim();
        this.columns = columns.trim();

        for (SubCommand c : SubCommand.values()){
            Matcher matcher = c.commandPattern.matcher(columns.trim());
            if (matcher.matches()){
                Debug.writeLine("Matcher identified. " + c.toString());
                subSelect = c;
                this.columns = matcher.group(1);
            }
        }
    }

    @Override
    public boolean execute() throws Exception {
        DatabaseConnector connector = DatabaseConnector.getInstance();

        if (connector.notUsingDB()){
            System.out.println("!Failed: No database currently being used.");
            return false;
        }

        Database current = connector.getCurrent();

        if (!current.containsTable(tableName)){
            commandMessage = RED + "! Database " + current.getDbName() + " does not contain table " + tableName;
            return false;
        }

        Table t = current.getTable(tableName);

        List<SQLColumn> targetColumns = new ArrayList<>();



        if (subSelect != null){
            if (!columns.equals("*")) {
                String[] attemptColumns = columns.split(",");

                for (String attemptColumn : attemptColumns) {
                    if (t.hasColumn(attemptColumn)) {
                        targetColumns.add(t.getColumn(attemptColumn));
                    } else {
                        commandMessage = RED + "! Table " + tableName + " contains no field " + attemptColumn;
                        return false;
                    }
                }
            }
            switch (subSelect){
                case SELECT_AVG -> {
                    SQLColumn column = targetColumns.get(0);
                    commandMessage = GREEN + "AVG(" + column.getTitle().split(" ")[0] + ")\n";
                    if (column instanceof FloatColumn){
                        commandMessage += calcFloatAvg((FloatColumn) column);
                    }
                    else if (column instanceof IntColumn){
                        commandMessage += calcIntAvg((IntColumn) column);
                    }
                    else {
                        commandMessage = RED + "! Cannot calculate average of string column.";
                    }
                    return true;
                }

                case SELECT_MAX -> {
                    SQLColumn column = targetColumns.get(0);
                    commandMessage = GREEN + "MAX(" + column.getTitle() + ")\n" + findMax(column).toString();
                    return true;
                }

                case SELECT_COUNT -> {
                    if (t.getColumns().size() > 0) {
                        SQLColumn column = t.getColumns().get(0);
                        commandMessage = GREEN + "COUNT(" + column.getTitle() + ")\n" + column.getData().size();
                        return true;
                    }
                    else {
                        commandMessage = RED + "! Table " + tableName + " does not have any columns";
                        return false;
                    }
                }

                default -> {
                    commandMessage = RED + "! Unrecognized select subcommand.";
                    return false;
                }
            }
        }

        if ((condition == null || condition.equals("")) && columns.equalsIgnoreCase("*"))
            System.out.println(DatabaseConnector.getInstance().getCurrent().selectAll(tableName));
        else if (condition == null || condition.equals(""))
            System.out.println(DatabaseConnector.getInstance().getCurrent().selectAll(tableName, columns));
        else
            System.out.println(DatabaseConnector.getInstance().getCurrent().select(tableName, columns, condition));

        return true;
    }

    private double calcFloatAvg(FloatColumn column){
        List<Object> data = column.getData();

        double total = 0;
        for (Object f : data){
            total += (Float) f;
        }
        return total/data.size();
    }

    private double calcIntAvg(IntColumn column){
        List<Object> data = column.getData();

        double total = 0;
        for (Object i : data){
            total += (Integer) i;
        }
        return total/data.size();
    }

    private Object findMax(SQLColumn column){
        List<Object> data = column.getData();
        if (data.isEmpty()) return "Column is empty!";

        Object maxItem = data.get(0);
        for (Object o : data){
            if (compare(maxItem, o, "<")){
                maxItem = o;
            }
        }
        return maxItem;
    }
}
