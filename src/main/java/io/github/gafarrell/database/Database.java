package io.github.gafarrell.database;

import io.github.gafarrell.database.column.SQLColumn;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        if (tables.containsKey(name)){
            return false;
        }
        return this.tables.putIfAbsent(name, new Table(name, columns, this)) == null;
    }


    public boolean alterTableDrop(String table, List<String> columns) throws Exception{
        if (tables.containsKey(table)){
            tables.get(table).alterTableDrop(columns);
            System.out.println("Table " + table + " modified.");
            return true;
        }
        System.out.println("Unable to modify table " + table + " because it does not exist.");
        return false;
    }

    public boolean alterTableAdd(String name, ArrayList<SQLColumn> newColumns) throws Exception {
        if (tables.containsKey(name)){
            tables.get(name).alterTableAdd(newColumns);
            return true;
        }
        return false;
    }

    public String select(String from, String where, String equals){
        return "Select with parameters not yet implemented";
    }
    public String selectAll(String from){
        if (tables.containsKey(from)){
            return tables.get(from).selectAll();
        }
        else return "!Failed to query table " + from + " because it does not exist.";
    }

    public boolean drop() throws Exception {
        for (Table t : tables.values()){
            if (!t.drop()) throw new Exception("Table " + t.getName() + " was unable to be dropped. Cancelling DB drop.");
        }
        tables.clear();
        return Files.deleteIfExists(Path.of("databases/" + dbName));
    }

    public boolean dropTable(String name) throws Exception {
        if (tables.containsKey(name)){
            return tables.remove(name).drop();
        }
        throw new Exception("!Failed to delete " + name + " because it does not exist.");
    }

    public String getDbName() {
        return dbName;
    }
    public String getDbDirectory(){return "databases/" + dbName + "/";}
}
