package io.github.gafarrell.database;

import io.github.gafarrell.database.column.SQLColumn;
import io.github.gafarrell.database.table.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    private File dbFile;
    private HashMap<String, Table> tables;

    public Database(File dbFile){
        this.dbFile = dbFile;
    }

    public void createTable(Table t){
        this.tables.put(t.getName(), t);
    }
}
