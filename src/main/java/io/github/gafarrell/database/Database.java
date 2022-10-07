package io.github.gafarrell.database;

import io.github.gafarrell.database.column.SQLColumn;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    private String dbName;
    private HashMap<String, Table> tables = new HashMap<>();

    public Database(String dbName) throws Exception {
        if (dbName.matches("[\\\\|/]")) throw new Exception("Invalid db name!");

        this.dbName = dbName;
        File dbFile = new File("databases/" + dbName);

        if (dbFile.exists()) {
            throw new Exception("!Failed Database " + dbName + " already exists!");
        }
        else{
            if (!Files.createDirectory(dbFile.toPath()).toFile().exists()) throw new Exception("Unable to create directory " + this.dbName);
        }
    }

    Database(File f) throws Exception {
        if (!f.exists() || !f.isDirectory()) throw new RuntimeException("Unable to load database, file is not valid or does not exist!");

        File[] directories = f.listFiles(pathname -> {
            if (pathname.getName().length() < 5) return false;
            return pathname.getName().substring(pathname.getName().length()-4).equalsIgnoreCase(".csv");
        });

        for (File table : directories) {
            if (tables.containsKey(table.getPath()))
                throw new Exception("Unable to load table, table " + table.getName() + " already exists in database " + dbName);
            tables.put(table.getName().substring(0, table.getName().length() - 4), new Table(table, this));
        }

        dbName = f.getName();
    }

    public boolean addTable(String name, ArrayList<SQLColumn> columns) throws Exception {
        return this.tables.putIfAbsent(name, new Table(name, columns, this)) == null;
    }

    public String select(String from, String where, String equals){
        if (tables.containsKey(from)){
            return tables.get(from).select(where, equals);
        }
        else return "Table " + from + " does not exist in database " + dbName;
    }

    public boolean drop() throws Exception {
        for (Table t : tables.values()){
            if (!t.drop()) throw new Exception("Table " + t.getName() + " was unable to be dropped. Cancelling DB drop.");
        }
        return Files.deleteIfExists(Path.of("databases/" + dbName));
    }

    public String getDbName() {
        return dbName;
    }
    public String getDbDirectory(){return "databases/" + dbName + "/";}
}
