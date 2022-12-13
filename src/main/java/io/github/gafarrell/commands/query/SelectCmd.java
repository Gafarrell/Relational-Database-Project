package io.github.gafarrell.commands.query;

import io.github.gafarrell.Debug;
import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

public class SelectCmd extends SQLCommand {

    private final String tableName, condition, columns;

    public SelectCmd(String tableName, String columns, String condition){
        this.tableName = tableName.trim();
        this.condition = condition == null ? "" : condition.trim();
        this.columns = columns.trim();
    }

    //TODO: Transfer control to the execute function rather than the database class.
    @Override
    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().notUsingDB()){
            System.out.println("!Failed: No database currently being used.");
            return false;
        }

        Debug.writeLine(condition);
        Debug.writeLine(columns);
        Debug.writeLine(tableName);

        if ((condition == null || condition.equals("")) && columns.equalsIgnoreCase("*"))
            System.out.println(DatabaseConnector.getInstance().getCurrent().selectAll(tableName));
        else if (condition == null || condition.equals(""))
            System.out.println(DatabaseConnector.getInstance().getCurrent().selectAll(tableName, columns));
        else
            System.out.println(DatabaseConnector.getInstance().getCurrent().select(tableName, columns, condition));

        return true;
    }
}
