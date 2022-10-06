package io.github.gafarrell.database;

import java.io.File;
import java.util.HashMap;

public class Database {
    private File dbDirectory;
    private HashMap<String, Table> tables = new HashMap<>();

    public Database(String dbName) throws Exception {
        if (dbName.matches("[\\\\|/]")) throw new Exception("Invalid db name!");

        this.dbDirectory = new File("databases/" + dbName);

        if (dbDirectory.exists()) {
            throw new Exception("!Failed Database " + dbName + " already exists!");
        }
        else{
            if (!dbDirectory.mkdirs()) throw new Exception("Unable to create directory " + dbDirectory.getName());
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
                throw new Exception("Unable to load table, table " + table.getName() + " already exists in database " + dbDirectory.getName());
            tables.put(table.getName().substring(0, table.getName().length() - 4), new Table(table, this));
        }

        dbDirectory = f;
    }

    public boolean createTable(Table t) throws Exception {
        System.out.println("Create table for datbase " + dbDirectory.getName());
        File tableFile = new File(dbDirectory + "/" + t.getName() + ".csv");
        if (!tableFile.createNewFile()) throw new Exception("Table " + t.getName() + " already exists in datbase " + dbDirectory.getName());
        else
            System.out.println("Created file " + tableFile.getPath());
        return this.tables.putIfAbsent(t.getName(), t) == null;
    }

    public boolean drop() throws Exception {
        for (Table t : tables.values()){
            if (!t.drop()) throw new Exception("Table " + t.getName() + " was unable to be dropped. Cancelling DB drop.");
        }
        return dbDirectory.delete();
    }

    public File getDbDirectory() {
        return dbDirectory;
    }
}
