package io.github.gafarrell;

import io.github.gafarrell.database.DatabaseConnector;
import io.github.gafarrell.parse.SQLScriptParser;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.println("==================================");
        System.out.println("\tInitializing Databases...");

        try {
            DatabaseConnector.Initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\tInitialized and ready!");
        System.out.println("==================================");

        System.out.println("~ Type an SQL query or enter a file path to run a .SQL script file.");

        if (args.length > 0){
            try {
                if (args[0].toLowerCase().contains(".sql")){
                    System.out.println("Command line file provided. Attempting to read now.");
                    System.out.println("File detected.");
                    File file = new File(args[0]);
                    if (!file.exists()) {
                        System.out.println("File does not exist!");
                    }
                    SQLScriptParser parser = new SQLScriptParser(file);
                    System.out.println("Executing...");
                    parser.executeAllCommands();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        Scanner scanner = new Scanner(System.in);
        String query = "";
        System.out.print("# ");
        while (!(query += scanner.nextLine()).equalsIgnoreCase(".exit")){
            query = query.replaceAll("\n", " ");
            try {
                if (query.toLowerCase().equalsIgnoreCase("/debug")) {
                    Debug.toggleDebug();
                }
                else if (query.toLowerCase().contains(".sql")){
                    System.out.println("File detected.");
                    File file = new File(query);
                    if (!file.exists()) {
                        System.out.println("File does not exist!");
                        continue;
                    }
                    SQLScriptParser parser = new SQLScriptParser(file);
                    System.out.println("Executing...");
                    parser.executeAllCommands();
                    continue;
                }
                else {
                    int semiIndex;
                    if ((semiIndex = query.indexOf(';')) == -1) continue;

                    query = query.substring(0, semiIndex);
                    SQLScriptParser toParse = new SQLScriptParser(query);
                    toParse.executeAllCommands();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            query = "";
            System.out.print("# ");
        }
    }
}
