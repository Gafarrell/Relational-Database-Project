package io.github.gafarrell.database;

import io.github.gafarrell.Debug;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class DatabaseConnector {
    private static DatabaseConnector instance;
    private HashMap<String, Database> activeDatabases = new HashMap<>();
    private List<Table> lockedTables = new ArrayList<>();
    private Database current;
    private final File lockFile = new File("databases/lockfile.csv");
    private final File dbGlobalDir = new File("databases/");

    private boolean transactionActive = false;
    public boolean dataChanged = false;

    // Initializes the database connector singleton.
    public static boolean Initialize() throws Exception {
        if (instance == null){
            instance = new DatabaseConnector();
            return true;
        }
        return false;
    }

    // Gets an instance of the database connector.
    public static DatabaseConnector getInstance() throws Exception {
        if (instance == null){
            instance = new DatabaseConnector();
        }
        return instance;
    }

    // Reads the files from the database directory and loads the data from internal files.
    // Alternatively will initialize the database directory if it does not already exist.
    private DatabaseConnector() throws Exception {
        if (!dbGlobalDir.exists())
        {
            System.out.println("\tUnable to find existing databases. Initializing new directory...");
            if (dbGlobalDir.mkdirs()){
                System.out.println("\tSuccessfully created new database directory.");
            }
            else {
                throw new Exception("\tUnable to create database directory!");
            }
        }
        else {
            System.out.println("\tDatabases directory found. Attempting to load existing databases...");
        }

        if (lockFile.createNewFile()){
            System.out.println("\tLock file generated.");
        }
        else {
            System.out.println("\tLock file found.");
        }

        Stream<Path> contents = Files.list(dbGlobalDir.toPath());
        ArrayList<File> directories = new ArrayList<>();

        contents.forEach(path -> {
            if (Files.isDirectory(path)){
                directories.add(path.toFile());
            }
        });

        if (directories.size() == 0) {
            System.out.println("\tNo databases found.");
            return;
        }

        for (File f : directories){
            if (activeDatabases.containsKey(f.getName())) throw new Exception("Duplicate database files in database directory!");
            activeDatabases.putIfAbsent(f.getName(), new Database(f));
        }

        System.out.println("\tSuccessfully loaded " + activeDatabases.keySet().size() + " database(s)");
    }

    public void lockTable(Table table) throws IOException {
        lockedTables.add(table);

        BufferedWriter lockFileWriter = new BufferedWriter(new FileWriter(lockFile));

        lockFileWriter.write(table.getParentDatabase().getDbName() + ":" + table.getTableName() + ",");

        lockFileWriter.close();
    }

    public void unlockAllTables() throws Exception {
        for (Table t : lockedTables){
            unlockTable(t);
        }
    }

    public void unlockTable(Table table) throws IOException {
        if (lockedTables.remove(table)){
            BufferedReader lockFileReader = new BufferedReader(new FileReader(lockFile));
            String line = lockFileReader.readLine();

            lockFile.delete();
            lockFile.createNewFile();

            BufferedWriter lockFileWriter = new BufferedWriter(new FileWriter(lockFile));
            line = line.replaceAll(table.getParentDatabase().getDbName() + ":" + table.getTableName() + ",", "");
            lockFileWriter.write(line);

            lockFileWriter.close();
            lockFileReader.close();
        }
    }

    public boolean isTableLocked(Table table) throws IOException {
        BufferedReader lockFileReader = new BufferedReader(new FileReader(lockFile));
        String line = lockFileReader.readLine();

        if (line != null && line.contains(table.getParentDatabase().getDbName() + ":" + table.getTableName()) && !lockedTables.contains(table)){
            lockFileReader.close();
            return true;
        }
        lockFileReader.close();
        return false;
    }

    public void updateDatabases() throws Exception {
        Stream<Path> contents = Files.list(dbGlobalDir.toPath());
        ArrayList<File> directories = new ArrayList<>();

        contents.forEach(path -> {
            if (Files.isDirectory(path)){
                directories.add(path.toFile());
            }
        });

        if (directories.size() == 0) {
            System.out.println("\tNo databases found.");
            return;
        }

        for (File f : directories){
            activeDatabases.putIfAbsent(f.getName(), new Database(f));
        }

        for (Database d : activeDatabases.values()){
            File f = new File(d.getDbDirectory());
            if (!directories.contains(f)) activeDatabases.remove(d.getDbName());
        }
    }

    public HashMap<String, Database> getActiveDatabases(){return activeDatabases;}
    public void setActiveDatabases(HashMap<String, Database> activeDatabases) { this.activeDatabases = activeDatabases; }

    public boolean isTransactionActive(){return transactionActive;}

    public void beginTransaction(){
        transactionActive = true;
    }

    public void commit() throws Exception {
        Debug.writeLine("Committing...");
        transactionActive = false;
        saveAll();
        unlockAllTables();
    }

    public void saveAll() throws IOException {
        for (Database db : activeDatabases.values()){
            Debug.writeLine("Saving database " + db.getDbName());
            db.save();
        }
    }

    public void abortCommit() throws Exception {
        transactionActive = false;
        dataChanged = false;

        activeDatabases.clear();

        Stream<Path> contents = Files.list(dbGlobalDir.toPath());
        ArrayList<File> directories = new ArrayList<>();

        contents.forEach(path -> {
            if (Files.isDirectory(path)){
                directories.add(path.toFile());
            }
        });

        if (directories.size() == 0) {
            System.out.println("\tNo databases found.");
            return;
        }

        for (File f : directories){
            if (activeDatabases.containsKey(f.getName())) throw new Exception("Duplicate database files in database directory!");
            activeDatabases.putIfAbsent(f.getName(), new Database(f));
        }
    }

    public boolean isAbortCommit(){
        return !dataChanged && transactionActive;
    }

    // Boolean for if the database connector is currently using a database.
    public boolean notUsingDB(){
        return current == null;
    }

    // Uses a specified DB
    public boolean use(String name) throws Exception {
        if (activeDatabases.containsKey(name)) {
            current = activeDatabases.get(name);
            return true;
        }
        throw new Exception("!Failed database " + name + " does not exist!");
    }

    // Gets the currently used DB.
    public Database getCurrent() {
        return current;
    }
}
