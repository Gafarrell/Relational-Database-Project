package io.github.gafarrell.commands.locking;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

public class CommitCmd extends SQLCommand {
    @Override
    public boolean execute() throws Exception {
        DatabaseConnector dbConnector = DatabaseConnector.getInstance();

        if (dbConnector.isTransactionActive()){
            dbConnector.saveAll();
            return true;
        }

        commandMessage = "No transaction active!";
        return false;
    }
}
