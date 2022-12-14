package io.github.gafarrell.commands.creation;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.Database;
import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.database.Table;

import java.util.HashMap;
import java.util.List;

public class DatabaseDropCmd extends SQLCommand {

    private String dbName;

    public DatabaseDropCmd(String dbName) throws Exception {
        this.dbName = dbName;
    }

    @Override
    public boolean execute() throws Exception {
        DatabaseConnector connector = DatabaseConnector.getInstance();
        HashMap<String, Database> activeDatabases = connector.getActiveDatabases();

        for (Database d : activeDatabases.values()){
            for (Table t : d.getTables().values()) {
                if (connector.isTableLocked(t)) {
                    commandMessage = RED + "! Database " + dbName + " contains a locked table.";
                    return false;
                }
            }
        }

        Database removed = activeDatabases.remove(dbName);
        if (removed != null && removed.delete()){
            commandMessage = GREEN + "Dropped " + dbName;
            return true;
        }

        commandMessage = RED + "! Database " + dbName + " was unable to be dropped.";
        return false;
    }
}
