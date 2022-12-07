package io.github.gafarrell.commands.query;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.database.DatabaseConnector;

public class JoinedSelect extends SQLCommand {

    String columns, join, where;

    public JoinedSelect(String columns, String join, String where)
    {
        this.columns = columns;
        this.join = join;
        this.where = where;
    }

    @Override
    public boolean execute() throws Exception {
        DatabaseConnector.getInstance().getCurrent().joinedSelect(join, columns, where);
        return false;
    }

    @Override
    public String getCommandString() {
        return null;
    }
}
