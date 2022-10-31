package io.github.gafarrell.commands.modification;

import io.github.gafarrell.commands.SQLCommand;

import java.util.List;

public class InsertCmd extends SQLCommand {

    private String tableName;

    public InsertCmd(List<String> params) {
        this.parameters = params.subList(1,params.size());
        tableName = params.get(0);
    }

    @Override
    public boolean execute() throws Exception {

        return false;
    }

    @Override
    public String getCommandString() {
        return null;
    }
}
