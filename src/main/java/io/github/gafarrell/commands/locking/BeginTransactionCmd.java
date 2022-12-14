package io.github.gafarrell.commands.locking;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

public class BeginTransactionCmd extends SQLCommand {
    @Override
    public boolean execute() throws Exception {
        DatabaseConnector connector = DatabaseConnector.getInstance();
        connector.beginTransaction();
        commandMessage = GREEN + "Transaction starts";
        return true;
    }
}
