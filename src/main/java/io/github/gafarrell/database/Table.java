package io.github.gafarrell.database;

import io.github.gafarrell.Debug;
import io.github.gafarrell.database.column.*;

import java.io.*;
import java.nio.file.Files;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Table {
    private String name;
    private File tableFile;
    private Database parentDatabase;
    private List<SQLColumn> columns = new ArrayList<>();

    /**
     * Constructs a table.
     * @param name Name of the table.
     * @param columns Data columns within the table.
     * @param parent Database this table belongs to.
     * @throws Exception If the table file was unable to be created.
     */
    public Table(String name, ArrayList<SQLColumn> columns, Database parent) throws Exception {
        this.name = name.trim();
        this.parentDatabase = parent;
        this.columns = columns;

        Debug.writeLine("Table created: " + name + ", " + parent.getDbName());
        Debug.writeLine("Contains " + columns.size() + " columns");

        tableFile = new File(parent.getDbDirectory() + "/" + name + ".csv");
        if (!tableFile.createNewFile()) throw new Exception("!Failed to create " + name + " because it already exists.");

        parent.put(this);
    }

    /**
     * Create table from existing table file.
     * @param f File storing the database information.
     * @param parentDatabase Database this file will belong to.
     * @throws IOException Exception iof the file is unable to be read/opened.
     */
    public Table(File f, Database parentDatabase) throws Exception {
        this.parentDatabase = parentDatabase;
        tableFile = f;
        name = f.getName().substring(0, f.getName().length()-4);

        BufferedReader reader = new BufferedReader(new FileReader(f));

        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            reader.close();
            return;
        }

        String[] columns = line.split(",");

        for (String column : columns){
            String parsable = column.replaceAll("[()]", " ").trim();
            String[] columnInfo = parsable.split(" ");
            switch (columnInfo[1].toLowerCase()) {
                case "int" -> this.columns.add(new IntColumn(column));
                case "float" -> this.columns.add(new FloatColumn(column));
                case "char", "varchar" -> this.columns.add(new StringColumn(column, Integer.parseInt(columnInfo[2])));
            }
        }

        while ((line = reader.readLine()) != null){
            List<String> data = Arrays.asList(line.split(","));
            addData(data);
        }

        reader.close();
    }

    // Column actions
    public SQLColumn getColumn(String title){
        return getColumnWithTitle(title);
    }
    public List<SQLColumn> getColumns(){return columns;}

    public boolean hasColumn(String title){
        title = title.trim();
        for (SQLColumn c : columns) {
            String columnTitle = c.getTitle().split(" ")[0];
            Debug.writeLine(columnTitle);
            if (columnTitle.equalsIgnoreCase(title))
                return true;
        }

        return false;
    }

    public void updateFromFile() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(tableFile));

        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            Debug.writeLine("Closing early");
            reader.close();
            return;
        }
        Debug.writeLine("Starting with these: " + line);
        String[] columns = line.split(",");
        this.columns = new ArrayList<>();

        for (String column : columns){
            String parsable = column.replaceAll("[()]", " ").trim();
            String[] columnInfo = parsable.split(" ");
            switch (columnInfo[1].toLowerCase()) {
                case "int" -> this.columns.add(new IntColumn(column));
                case "float" -> this.columns.add(new FloatColumn(column));
                case "char", "varchar" -> this.columns.add(new StringColumn(column, Integer.parseInt(columnInfo[2])));
            }
        }

        while ((line = reader.readLine()) != null){
            List<String> data = Arrays.asList(line.split(","));
            addData(data);
        }

        reader.close();
    }

    public void setColumns(List<SQLColumn> columns){this.columns = columns;}

    public String getTableName() {
        return name;
    }
    public Database getParentDatabase() {return parentDatabase;}

    /**
     * Selects all data from the table.
     * @return String format of all table data.
     */
    public String selectAll() throws Exception {
        updateFromFile();
        StringBuilder infoString = new StringBuilder();

        for (SQLColumn column : columns){
            infoString.append(column.getTitle()).append(" | ");
        }

        if (columns.size() > 0) {
            for (int i = 0; i < columns.get(0).getColumnSize(); i++){
                infoString.append("\n");
                for (SQLColumn column : columns) {
                    infoString.append(column.getDataAtRow(i))
                            .append(" | ");
                }
            }
        }
        return infoString.toString();
    }

    /**
     * Select all values from columns with given titles.
     * @param columns Comma separated list of columns.
     * @return Printable format of column data.
     */
    public String selectAll(String columns) {
        StringBuilder builder = new StringBuilder();

        String[] cs = columns.split(",");
        List<String> cols = Arrays.asList(cs);
        List<SQLColumn> targetColumns = new ArrayList<>();

        for (String s : cols){
            SQLColumn col = getColumnWithTitle(s);
            if (col != null) {
                targetColumns.add(col);
                builder.append(col.getTitle()).append(" | ");
            }
        }

        if (targetColumns.isEmpty()) return "Invalid column selection";

        int size = this.columns.get(0).getColumnSize();
        builder.append("\n");

        for (int i = 0; i < size; i++) {
            for (SQLColumn col : targetColumns)
                builder.append(col.getDataAtRow(i)).append(" | ");
            builder.append("\n");
        }

        return builder.toString();
    }

    /**
     * Select values that match the conditions from the specified columns.
     * @param showColumnString Columns to select.
     * @param conditions Condition on which rows to select from the columns.
     * @return Returns printable format of selected rows/columns.
     */
    public String select(String showColumnString, String conditions) {
        StringBuilder builder = new StringBuilder();

        List<String> showColumnsStringList = Arrays.asList(showColumnString.split(","));
        List<SQLColumn> showColumns = new ArrayList<>();

        for (String columnTitle : showColumnsStringList){
            SQLColumn found = getColumnWithTitle(columnTitle.trim());
            if (found != null) {
                showColumns.add(found);
                builder.append(found.getTitle()).append(" | ");
            }
        }
        builder.append("\n");

        String[] conditionsSplit = conditions.split(" ");
        if (conditionsSplit.length != 3) return "Invalid condition statement:\n\t" + conditions;

        SQLColumn searchColumn = getColumnWithTitle(conditionsSplit[0]);
        if (searchColumn == null) return "Column " + conditionsSplit[0] + " does not exist";

        int[] selections = searchColumn.select(conditionsSplit[1], conditionsSplit[2]);

        for (int selection : selections){
            for (SQLColumn col : showColumns)
                builder.append(col.getDataAtRow(selection)).append(" | ");
            builder.append("\n");
        }

        return builder.toString();
    }

    /**Save the table's file to disk.
     * @throws IOException General file writing exception.
     */
    public void save() throws IOException {
        if (columns.isEmpty() && tableFile.createNewFile()) return;

        if (!(tableFile.delete() && tableFile.createNewFile())) return;

        BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile));
        for (SQLColumn column : columns){
            writer.write(column.getTitle() + ",");
        }
        writer.write("\n");

        if (columns.size() == 0) {
            writer.close();
            return;
        }

        for (int i = 0; i < columns.get(0).getColumnSize(); i++) {
            for (SQLColumn c : columns) {
                writer.write(c.getDataAtRow(i) + ",");
            }
            writer.write("\n");
        }
        writer.close();
    }

    public void delete() throws Exception {
        if (!tableFile.delete()){
            throw new Exception("Table file " + tableFile.getName() + " unable to be deleted");
        }
    }

    // Private functions.
    private void addData(List<String> values) throws Exception {
        if (values.size() != columns.size()) throw new Exception("Not enough arguments!");
        for (int i = 0; i < values.size(); i++){
            if (!columns.get(i).queueData(values.get(i))){
                dequeueDataFromColumns();
                throw new Exception("Argument mismatch!");
            }
        }
        insertQueuedData();
    }

    private void dequeueDataFromColumns(){
        for (SQLColumn column : columns){
            column.clearQueue();
        }
    }

    private void insertQueuedData(){
        for (SQLColumn column : columns){
            column.insertQueue();
        }
    }

    private SQLColumn getColumnWithTitle(String title){
        Debug.writeLine("Finding column with title: " + title);
        for (SQLColumn c : columns){
            String columnTitle = c.getTitle().split(" ")[0];
            Debug.writeLine(columnTitle);
            if (title.equals(columnTitle))
                return c;
        }
        return null;
    }

    // Overrides
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
}
