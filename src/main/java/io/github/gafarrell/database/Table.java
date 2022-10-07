package io.github.gafarrell.database;

import io.github.gafarrell.database.column.FloatColumn;
import io.github.gafarrell.database.column.IntColumn;
import io.github.gafarrell.database.column.SQLColumn;
import io.github.gafarrell.database.column.StringColumn;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

        if (columns.size() > 1)
            for (int i = 0; i < columns.size()-1; i++) columns.get(i).setNextColumn(columns.get(i+1));

        File file = new File(parent.getDbDirectory() + name + ".csv");
        if (!file.createNewFile()) throw new Exception("!Failed to create " + name + " because it already exists.");

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

        if (line == null || line.isEmpty()) {
            reader.close();
            return;
        }

        List<String> columns = Arrays.stream(line.split(",")).toList();

        for (String column : columns){
            String[] columnInfo = column.split(" ");
            switch (columnInfo[1].toLowerCase()){
                case "int":
                    this.columns.add(new IntColumn(column));
                    break;
                case "float":
                    this.columns.add(new FloatColumn(column));
                    break;
                case "char":
                case "varchar":
                    this.columns.add(new StringColumn(column, Integer.parseInt(columnInfo[2])));
                    break;
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
        StringBuilder infoString = new StringBuilder("=================\n").append("\t").append(name).append("\n=================");

        if (columns.size() > 0) {
            infoString.append("\n");
            for (SQLColumn column : columns) {
                infoString.append(column.getTitle()).append(" | ");
            }
            infoString.append('\n');
        }
        return infoString.toString();
    }

    public String select(String where, String equals){
        return "Select by specifications not yet implemented";
    }

    public boolean alterTableAdd(ArrayList<SQLColumn> newColumns) throws Exception {
        File file = new File(parentDatabase.getDbDirectory() + name + ".csv");
        file.delete();
        if (!file.createNewFile()) throw new Exception("Table " + name + " already exists in database " + parentDatabase.getDbName());

        this.columns.addAll(newColumns);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (SQLColumn column : columns){
            writer.write(column.getTitle() + ",");
        }

        writer.write("\n");
        writer.close();

        if (columns.size() > 1)
            for (int i = 0; i < columns.size()-1; i++) columns.get(i).setNextColumn(columns.get(i+1));

        return true;
    }

    public boolean alterTableDrop(List<String> names) throws Exception {
        File file = new File(parentDatabase.getDbDirectory() + name + ".csv");
        file.delete();
        if (!file.createNewFile()) throw new Exception("Table " + name + " already exists in database " + parentDatabase.getDbName());
        ArrayList<SQLColumn> toRemove = new ArrayList<>();

        columns.forEach(column -> {
            if (names.contains(column.getTitle().split(" ")[0])) toRemove.add(column);
        });

        if (!columns.removeAll(toRemove)) throw new Exception("!Failed to alter table because the specified columns were not found.");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (SQLColumn column : columns){
            writer.write(column.getTitle() + ",");
        }

        writer.write("\n");
        writer.close();

        if (columns.size() > 1)
            for (int i = 0; i < columns.size()-1; i++) columns.get(i).setNextColumn(columns.get(i+1));

        return true;
    }

    public String selectAll(){
        StringBuilder infoString = new StringBuilder();
        if (columns.size() > 0) {
            infoString.append(columns.get(0).getAllValues());
        }
        return infoString.toString();
    }
}
