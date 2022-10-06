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
            DatabaseConnector.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\tInitialized and ready!");
        System.out.println("==================================");

        Scanner scanner = new Scanner(System.in);
        String query = "";
        System.out.print("# ");
        while (!(query = scanner.nextLine()).equalsIgnoreCase(".exit")){
            int semiIndex;
            if ((semiIndex = query.indexOf(';')) == -1) continue;
            query = query.substring(0, semiIndex);
            try {
                SQLScriptParser toParse = new SQLScriptParser(query);
                toParse.execute();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            System.out.print("# ");
        }

    }
}
