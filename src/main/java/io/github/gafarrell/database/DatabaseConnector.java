package io.github.gafarrell.database;

import java.io.File;

public class DatabaseConnector {
    private static DatabaseConnector instance;

    private Database current;
    private File dbServerPath;

    public static DatabaseConnector getInstance(){
        if (instance == null){
            instance = new DatabaseConnector();
        }
        return instance;
    }

    private DatabaseConnector(){

    }

    public boolean use(){

        return true;
    }

    public Database getCurrent() {
        return current;
    }
}
