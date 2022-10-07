package io.github.gafarrell.database;

import io.github.gafarrell.database.column.FloatColumn;
import io.github.gafarrell.database.column.IntColumn;
import io.github.gafarrell.database.column.SQLColumn;
import io.github.gafarrell.database.column.StringColumn;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table {
    private String name;
    private Database parentDatabase;
    private ArrayList<SQLColumn> columns = new ArrayList<>();

    public Table(String name, ArrayList<SQLColumn> columns, Database parent) throws Exception {
        this.name = name;
        this.parentDatabase = parent;
        this.columns = columns;

        File file = new File(parent.getDbDirectory() + name + ".csv");
        if (!file.createNewFile()) throw new Exception("Table " + name + " already exists in database " + parent.getDbName());

        if (columns == null || columns.isEmpty()) {
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (SQLColumn column : columns){
            writer.write(column.getTitle() + ",");
        }
        writer.write("\n");
        writer.close();
    }

    Table(File f, Database parentDatabase) throws IOException {
        this.parentDatabase = parentDatabase;
        name = f.getName().substring(0, f.getName().length()-4);

        BufferedReader reader = new BufferedReader(new FileReader(f));

        String line = reader.readLine();

        if (line == null) {
            reader.close();
            return;
        }
        List<String> columns = Arrays.stream(line.split(",")).toList();

        for (String column : columns){
            String[] columnInfo = column.split(" ");
            switch (columnInfo[0].toLowerCase()){
                case "int":
                    this.columns.add(new IntColumn(columnInfo[1]));
                case "float":
                    this.columns.add(new FloatColumn(columnInfo[1]));
                case "varchar":
                    this.columns.add(new StringColumn(columnInfo[1], Integer.parseInt(columnInfo[2])));
            }
        }

        reader.close();

        for (int i = 0; i < this.columns.size()-1; i++){
            this.columns.get(i).setNextColumn(this.columns.get(i));
        }
    }

    public String getName() {
        return name;
    }

    public boolean drop(){
        File tableFile = new File("databases/" + parentDatabase.getDbName() + "/" + name + ".csv");
        if (tableFile.exists()){
            try {
                Files.delete(tableFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public String toString(){
        StringBuilder infoString = new StringBuilder("=======\n").append("Name: ").append(name).append("\n=======");

        for (SQLColumn column : columns){
            infoString.append(column.getTitle()).append(" | ");
        }
        infoString.append('\n');
        return infoString.toString();
    }

    public String select(String where, String equals){
        StringBuilder selection = new StringBuilder();
        selection.append(this);
        if (where.equalsIgnoreCase("*")){
            selection.append(columns.get(0).getAllValues());
        }
        return selection.toString();
    }
}
