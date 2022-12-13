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
    private boolean locked = false;
    private List<SQLColumn> columns = new ArrayList<>();

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

        tableFile = new File(parent.getDbDirectory() + name + ".csv");
        if (!tableFile.createNewFile()) throw new Exception("!Failed to create " + name + " because it already exists.");

        if (columns.isEmpty()) {
            return;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile, true));

        for (SQLColumn column : columns){
            writer.write(column.getTitle() + ",");
        }
        writer.write("\n");
        writer.close();
    }

    /**
     * Table joining constructor
     * @param leftTable First table.
     * @param rightTable Second table.
     */
    public Table(Table leftTable, Table rightTable, String joinString, String onString) throws Exception {
        String[] onOperations = onString.trim().split(" ");
        Debug.writeArray(onOperations);

        String parseJoin = parseJoinString(joinString);
        Debug.writeLine(parseJoin);
        Debug.writeLine("We finally made it!!!!");
        if (onOperations.length != 3) return;

        SQLColumn leftCol = leftTable.getColumnWithTitle(onOperations[0].split("\\.")[1]);
        String operator = onOperations[1];
        SQLColumn rightCol = rightTable.getColumnWithTitle(onOperations[2].split("\\.")[1]);

        if (leftCol != null && rightCol != null)
        {
            if (parseJoin.equals(",")){
                for (SQLColumn c : leftTable.columns)
                    columns.add(c.deepClone());
                for (SQLColumn c : rightTable.columns)
                    columns.add(c.deepClone());
            }
            for (SQLColumn c : leftTable.columns)
                columns.add(c.clone());
            for (SQLColumn c : rightTable.columns)
                columns.add(c.clone());

            Map<Integer, List<Integer>> insertions = leftCol.selectWhere(operator, rightCol);
            Set<Integer> keys = insertions.keySet();
            List<String> data = new ArrayList<>();

            if (parseJoin.equalsIgnoreCase("left outer join")){
                for (Integer i : keys) {
                    for (Integer j : insertions.get(i)){
                        for (SQLColumn c : leftTable.columns)
                            data.add(c.getDataAtRow(i));
                        for (SQLColumn c : rightTable.columns)
                            data.add(c.getDataAtRow(j));
                        addData(data);
                    }
                }
            }
            else if (parseJoin.equalsIgnoreCase("inner join")){
                for (Integer i : keys) {
                    if (!insertions.get(i).isEmpty())
                    for (Integer j : insertions.get(i)){
                        for (SQLColumn c : leftTable.columns)
                            data.add(c.getDataAtRow(i));
                        for (SQLColumn c : rightTable.columns)
                            data.add(c.getDataAtRow(j));
                        addData(data);
                    }
                }
            }
        }
    }

    /**
     * Create table from existing table file.
     * @param f File storing the database information.
     * @param parentDatabase Database this file will belong to.
     * @throws IOException Exception iof the file is unable to be read/opened.
     */
    Table(File f, Database parentDatabase) throws Exception {
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

        while ((line = reader.readLine()) != null){
            List<String> data = Arrays.asList(line.split(","));
            addData(data);
        }

        reader.close();

        for (int i = 0; i < this.columns.size()-1; i++){
            this.columns.get(i).setNextColumn(this.columns.get(i));
        }
    }

    public SQLColumn getColumn(String title){
        for (SQLColumn c : columns)
            if (c.getTitle().equalsIgnoreCase(title))
                return c;

        return null;
    }

    public boolean hasColumn(String title){
        for (SQLColumn c : columns)
            if (c.getTitle().equalsIgnoreCase(title))
                return true;

        return false;
    }

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

    public void lock(){locked = true;}
    public void unlock(){locked = false;}

    public boolean isLocked(){return locked;}

    public List<SQLColumn> getColumns(){return columns;}

    public void setColumns(List<SQLColumn> columns){this.columns = columns;}

    public boolean containsColumn(String title){
        for (SQLColumn c : columns)
            if (c.getTitle().equals(title))
                return true;
        return false;
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

    /**
     * Selects all data from the table.
     * @return String format of all table data.
     */
    public String selectAll() {
        StringBuilder infoString = new StringBuilder();

        for (SQLColumn column : columns){
            infoString.append(column.getTitle()).append(" | ");
        }

        if (columns.size() > 0) {
            for (int i = 0; i < columns.get(0).getColumnSize(); i++){
                infoString.append("\n");
                for (SQLColumn column : columns)
                    infoString.append(column.getDataAtRow(i))
                                .append(" | ");
            }
        }
        return infoString.toString();
    }

    public void save() throws IOException {
        if (columns.isEmpty() && tableFile.createNewFile()) return;

        if (!(tableFile.delete() && tableFile.createNewFile())) return;

        BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile));
        for (SQLColumn column : columns){
            writer.write(column.getTitle() + ",");
        }
        for (int i = 0; i < columns.get(0).getColumnSize(); i++) {
            for (SQLColumn c : columns) {
                writer.write(c.getDataAtRow(i) + ",");
            }
        }

    }

    private SQLColumn getColumnWithTitle(String title){
        for (SQLColumn c : columns){
            String columnTitle = c.getTitle().split(" ")[0];
            if (title.equals(columnTitle))
                return c;
        }
        return null;
    }

    private String parseJoinString(String joinString){
        if (joinString.contains(",")) return ",";
        if (joinString.contains("left outer join")) return "left outer join";
        else return "inner join";
    }
}
