package io.github.gafarrell.database;

import io.github.gafarrell.database.column.*;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class Table {
    private String name;
    private Database parentDatabase;
    private ArrayList<SQLColumn> columns = new ArrayList<>();

    /**
     * Constructs a table.
     * @param name Name of the table.
     * @param columns Data columns within the table.
     * @param parent Database this table belongs to.
     * @throws Exception If the table file was unable to be created.
     */
    public Table(String name, ArrayList<SQLColumn> columns, Database parent) throws Exception {
        this.name = name;
        this.parentDatabase = parent;
        this.columns = columns;

        if (columns.size() > 1)
            for (int i = 0; i < columns.size()-1; i++) columns.get(i).setNextColumn(columns.get(i+1));

        File file = new File(parent.getDbDirectory() + name + ".csv");
        if (!file.createNewFile()) throw new Exception("!Failed to create " + name + " because it already exists.");

        if (columns.isEmpty()) {
            return;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (SQLColumn column : columns){
            writer.write(column.getTitle() + ",");
        }
        writer.write("\n");
        writer.close();
    }

    /**
     * Create table from existing table file.
     * @param f File storing the database information.
     * @param parentDatabase Database this file will belong to.
     * @throws IOException Exception iof the file is unable to be read/opened.
     */
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
            String parsable = column.replaceAll("[()]", " ").trim();
            String[] columnInfo = parsable.split(" ");
            switch (columnInfo[1].toLowerCase()) {
                case "int" -> this.columns.add(new IntColumn(column));
                case "float" -> this.columns.add(new FloatColumn(column));
                case "char", "varchar" -> this.columns.add(new StringColumn(column, Integer.parseInt(columnInfo[2])));
            }
        }

        reader.close();

        for (int i = 0; i < this.columns.size()-1; i++){
            this.columns.get(i).setNextColumn(this.columns.get(i));
        }
    }

    public int columnCount(){ return columns.size(); }

    /**
     * Insert data values into the table.
     * @param values String values of the data to be inserted into the table.
     * @throws Exception If the data is unable to be parsed.
     */
    public void insertInto(List<String> values) throws Exception {
        if (values.size() <= 0) throw new Exception("Values cannot be empty!");
        if (values.size() != columns.size()) throw new Exception("Table size does not match given number of arguments!");
        System.out.println("Inserting values to the columns.");
        SQLColumn firstColumn = columns.get(0);
        firstColumn.insert(values);
    }

    /**
     * @return Name of the table.
     */
    public String getName() {
        return name;
    }

    /**
     * @return True if the table was successfully deleted. False otherwise.
     */
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

    /**
     * @return String format of the table's columns.
     */
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

    /**
     * Add columns to table.
     * @param newColumns New columns to add.
     * @return True if table alter was successful, false otherwise.
     * @throws Exception If table already exists or unable to add column.
     */
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

    /**
     * Drops listed columns from table.
     * @param names Names of columns to drop.
     * @return True if columns were all successfully dropped, false otherwise.
     * @throws Exception If the table drop was unsuccessful in the file.
     */
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

    /**
     * Selects all data from the table.
     * @return String format of all table data.
     */
    public String selectAll(){
        StringBuilder infoString = new StringBuilder();

        for (SQLColumn column : columns){
            infoString.append(column.getTitle()).append(" | ");
        }

        if (columns.size() > 0) {
            for (int i = 0; i < columns.get(0).getColumnSize(); i++){
                for (SQLColumn column : columns)
                    infoString.append("\n")
                                .append(column.getDataAtRow(i))
                                .append(" | ");
            }
        }
        return infoString.toString();
    }
}
