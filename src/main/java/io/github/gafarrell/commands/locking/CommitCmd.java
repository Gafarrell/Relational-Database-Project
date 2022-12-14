package io.github.gafarrell.commands.locking;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

public class CommitCmd extends SQLCommand {
    @Override
    public boolean execute() throws Exception {
        DatabaseConnector dbConnector = DatabaseConnector.getInstance();

        if (dbConnector.isTransactionActive()){
            dbConnector.commit();
            commandMessage = GREEN + "Transaction committed";
            return true;
        }

        commandMessage = RED + "Transaction abort!";
        return false;
    }
}
