package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

import java.util.List;

public class TableDropCmd extends SQLCommand {
    public TableDropCmd(List<String> parameters){
        this.parameters = parameters;
    }

    @Override
    public boolean execute() throws Exception {

        if (!DatabaseConnector.getInstance().isUsing()){
            System.out.println("!Failed: No database currently being used.");
            return false;
        }

        if (parameters.size() != 1) throw new Exception("!Failed: Parameters are not valid for table drop.");
        if (DatabaseConnector.getInstance().getCurrent().dropTable(parameters.get(0))){
            System.out.println("Table " + parameters.get(0) + " deleted.");
            return true;
        }
        return false;
    }

    @Override
    public String getCommandString() {
        return "DROP TABLE " + parameters.get(0);
    }
}
