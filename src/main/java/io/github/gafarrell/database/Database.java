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

    /**
     * Creates a database.
     * @param dbName The database name.
     * @throws Exception Invalid DB Name or DB already exists.
     */
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

    /**
     * Creates a database.
     * @param f The file the database data is stored in.
     * @throws Exception If the database already exists or unable to access file.
     */
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

    /**
     * Adds a table to the database.
     * @param name Name of the table.
     * @param columns List of SQLColumns that the table will store.
     * @return True if table successfully created. False otherwise.
     * @throws Exception If the table data is not parsable.
     */
    public boolean addTable(String name, ArrayList<SQLColumn> columns) throws Exception {
        if (tables.containsKey(name)){
            return false;
        }
        return this.tables.putIfAbsent(name, new Table(name, columns, this)) == null;
    }

    /**
     * Drops columns from table with the given name.
     * @param table Name of the table.
     * @param columns List of column names to drop from table.
     * @throws Exception If table is unable to alter file.
     */
    public void alterTableDrop(String table, List<String> columns) throws Exception{
        if (tables.containsKey(table)){
            tables.get(table).alterTableDrop(columns);
            System.out.println("Table " + table + " modified.");
            return;
        }
        System.out.println("Unable to modify table " + table + " because it does not exist.");
    }

    /**
     * Add data columns to a table.
     * @param name Name of the table.
     * @param newColumns Columns to add to the table.
     * @return Returns true of table was successfully created, false otherwise.
     * @throws Exception If table was unable to create file information.
     */
    public boolean alterTableAdd(String name, ArrayList<SQLColumn> newColumns) throws Exception {
        if (tables.containsKey(name)){
            tables.get(name).alterTableAdd(newColumns);
            return true;
        }
        return false;
    }

    /**
     * Inserts data values to a given table.
     * @param tableName Name of the table.
     * @param values String formatted data of the data to be added to the table.
     * @throws Exception If the data is unable to be parsed or in the incorrect order.
     */
    public void insertInto(String tableName, List<String> values) throws Exception {
        if (tables.containsKey(tableName)){
            System.out.println("Table exists, adding values.");
            tables.get(tableName).insertInto(values);
        }
    }

    /**
     * Select data from a table given the specified search parameters.
     * @param from Name of the table.
     * @param where Data column to check within the table.
     * @param equals Value to check the data column against.
     * @return Returns the data string of the information gathered.
     */
    public String select(String from, String where, String equals){
        return "Select with parameters not yet implemented";
    }

    /**
     * Select all data from a given table.
     * @param from Table name.
     * @return Returns the string formatted data.
     */
    public String selectAll(String from){
        if (tables.containsKey(from)){
            return tables.get(from).selectAll();
        }
        else return "!Failed to query table " + from + " because it does not exist.";
    }

    /**
     * Drops the current database. Essentially self-destruct.
     * @return True if self removal is successful.
     * @throws Exception If table does not exist or if file is unable to be deleted.
     */
    public boolean drop() throws Exception {
        for (Table t : tables.values()){
            if (!t.drop()) throw new Exception("Table " + t.getName() + " was unable to be dropped. Cancelling DB drop.");
        }
        tables.clear();
        return Files.deleteIfExists(Path.of("databases/" + dbName));
    }

    /**
     * Drops the given table.
     * @param name Name of the table.
     * @return True if table was successfully deleted.
     * @throws Exception If table file was not deletable.
     */
    public boolean dropTable(String name) throws Exception {
        if (tables.containsKey(name)){
            return tables.remove(name).drop();
        }
        throw new Exception("!Failed to delete " + name + " because it does not exist.");
    }

    /**
     * @return Name of the database.
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @return Relative directory of the database.
     */
    public String getDbDirectory(){return "databases/" + dbName + "/";}
}
