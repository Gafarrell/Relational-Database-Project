package io.github.gafarrell.database.table;

import io.github.gafarrell.database.column.SQLColumn;

import java.util.ArrayList;

public class Table {
    private String name;
    private ArrayList<SQLColumn> columns;

    public Table(String name, ArrayList<SQLColumn> columns){
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }
}
