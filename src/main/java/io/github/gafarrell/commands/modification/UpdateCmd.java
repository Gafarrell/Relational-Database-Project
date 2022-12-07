package io.github.gafarrell.commands.modification;

import io.github.gafarrell.Debug;
import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

public class UpdateCmd extends SQLCommand {
    private final String table, set, where;
    public UpdateCmd(String table, String set, String where){
        this.table = table.trim();
        this.set = set.trim();
        this.where = where.trim();
    }

    @Override
    public boolean execute() throws Exception {

        Debug.writeLine("Updating table " + table + " with parameters set: \"" + set + "\" and where: \"" + where);

        try {
            if (DatabaseConnector.getInstance().getCurrent().update(table, set, where)){
                System.out.println("Successfully updated table " + table);
            }
            else{
                System.out.println("Unable to update table " + table);
            }
        }
        catch (Exception e) {
            System.out.println("An exception occurred while trying to update table " + table);
        }
        return true;
    }

    @Override
    public String getCommandString() {
        return null;
    }
}
