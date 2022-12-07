package io.github.gafarrell.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class DatabaseConnector {
    private static DatabaseConnector instance;
    private static HashMap<String, Database> activeDatabases = new HashMap<>();
    private static Database current;

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
        File dbGlobalDir = new File("databases/");

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

    // Creates a specified DB.
    public boolean createDatabase(String name) throws Exception {
        File file = new File("databases/" + name);
        if (file.exists()) return false;

        return (activeDatabases.putIfAbsent(name, new Database(name)) == null);
    }


    // Drops a specified DB.
    public boolean dropDatabase(String name) throws Exception {
        if (activeDatabases.containsKey(name)){
            activeDatabases.get(name).dropDatabase();
            return activeDatabases.remove(name) != null;
        }
        else return false;
    }

    // Gets the currently used DB.
    public Database getCurrent() {
        return current;
    }
}
