package io.github.gafarrell;

import io.github.gafarrell.parse.SQLScriptParser;

import java.io.File;

public class Main {
    public static void main(String[] args){
        try {
            SQLScriptParser parser = new SQLScriptParser(new File("C:\\users\\farrell_G\\desktop\\PA1_test.sql"));
            parser.nextCommand();
            parser.nextCommand();
            parser.nextCommand();
            parser.nextCommand();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
