package io.github.gafarrell.commands.modification;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;

import java.io.Console;
import java.util.List;

public class InsertCmd extends SQLCommand {

    private String tableName;

    public InsertCmd(String tableName, List<String> values) {
        this.tableName = tableName;
        this.parameters = values;
    }

    @Override
    public boolean execute() throws Exception {
        DatabaseConnector.getInstance().getCurrent().insertInto(tableName, parameters);
        return true;
    }

    @Override
    public String getCommandString() {
        return null;
    }
}
