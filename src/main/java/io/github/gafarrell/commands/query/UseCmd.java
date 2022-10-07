package io.github.gafarrell.commands.query;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

public class UseCmd extends SQLCommand {
    public UseCmd(String dbName){
        this.parameters.add(dbName);
    }

    @Override
    public boolean execute() throws Exception {
        if (DatabaseConnector.getInstance().use(parameters.get(0))){
            System.out.println("Using database " + parameters.get(0) + ".");
            return true;
        }
        return false;
    }

    @Override
    public String getCommandString() {
        return "USE " + parameters.get(0);
    }
}
