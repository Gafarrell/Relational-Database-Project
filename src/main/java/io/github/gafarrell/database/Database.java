package io.github.gafarrell.database;

import io.github.gafarrell.Debug;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

public class Database {
    private String dbName;
    private final HashMap<String, Table> tables = new HashMap<>();
    private File dbFile;

    /**
     * Creates a database.
     * @param dbName The database name.
     * @throws Exception Invalid DB Name or DB already exists.
     */
    public Database(String dbName) throws Exception {
        if (dbName.matches("[\\\\|/]")) throw new Exception("Invalid db name!");

        this.dbName = dbName.trim();
        this.dbFile = new File("databases/" + dbName);

        if (dbFile.exists()) {
            throw new Exception("!Failed Database " + dbName + " already exists!");
        }
        else{
            if (!Files.createDirectory(dbFile.toPath()).toFile().exists())
                throw new Exception("Unable to create directory " + this.dbName);
        }
    }

    /**
     * Creates a database.
     * @param f The file the database data is stored in.
     * @throws Exception If the database already exists or unable to access file.
     */
    public Database(File f) throws Exception {
        if (!f.exists() || !f.isDirectory()) throw new RuntimeException("! Unable to load database, file is not valid or does not exist");

        File[] directories = f.listFiles(pathname -> {
            if (pathname.getName().length() < 5) return false;
            return pathname.getName().substring(pathname.getName().length()-4).equalsIgnoreCase(".csv");
        });

        assert directories != null;
        for (File table : directories) {
            if (tables.containsKey(table.getPath()))
                throw new Exception("Unable to load table, table " + table.getName() + " already exists in database " + dbName);
            tables.put(table.getName().substring(0, table.getName().length() - 4), new Table(table, this));
        }

        dbFile = f;
        dbName = f.getName();
    }

    /**
     * Select data from a table given the specified search parameters.
     * @param from Name of the table.
     * @return Returns the data string of the information gathered.
     */
    public String select(String from, String columns, String conditional){
        if (tables.containsKey(from)){
            return tables.get(from).select(columns, conditional);
        }
        return "Table " + from + " does not exist.";
    }

    public String selectAll(String from, String columns) {
        Debug.writeLine("Selecting all from column list.");
        if (tables.containsKey(from)){
            return tables.get(from).selectAll(columns);
        }
        else return "!Failed to query table " + from + " because it does not exist.";
    }

    public void save() throws IOException {
        for (Table t : tables.values()){
            Debug.writeLine("Saving table " + t.getTableName());
            t.save();
        }
    }

    /**
     * Select all data from a given table.
     * @param from Table name.
     * @return Returns the string formatted data.
     */
    public String selectAll(String from) throws Exception {
        Debug.writeLine("Just selecting all.");
        if (tables.containsKey(from)){
            return tables.get(from).selectAll();
        }
        else return "!Failed to query table " + from + " because it does not exist.";
    }

    void put(Table t){
        tables.put(t.getTableName(), t);
    }

    public boolean delete(){
        return clearSubfiles(dbFile);
    }

    private boolean clearSubfiles(File f){
        if (!f.isDirectory()) return false;
        for (File subFile : Objects.requireNonNull(f.listFiles())){
            if (subFile.isDirectory())
                clearSubfiles(subFile);
            else
                subFile.delete();
        }

        return f.delete();
    }

    // Getters
    public String getDbName() {
        return dbName;
    }
    public String getDbDirectory(){return dbFile.toPath().toString();}
    public Table getTable(String table){return tables.get(table);}
    public boolean containsTable(String table){return tables.containsKey(table);}
    public HashMap<String, Table> getTables(){return tables;}
}
