package io.github.gafarrell.database.column;

import java.io.BufferedWriter;
import java.util.ArrayList;

public abstract class SQLColumn<T> {
    public enum ColumnType{
        DATE_TIME,
        FLOAT,
        INT,
        STRING
    }

    private SQLColumn nextColumn, prevColumn;
    protected String title;
    private final ArrayList<T> data = new ArrayList<>();
    private int columnLength;

    public SQLColumn(String title)
    {
        this.title = title;
    }

    public void attachColumn(SQLColumn prevColumn, SQLColumn nextColumn){
        nextColumn.prevColumn = this;
        prevColumn.nextColumn = this;
    }

    public void setNextColumn(SQLColumn nextColumn) {
        if (this.nextColumn != null) this.nextColumn.prevColumn = nextColumn;
        this.nextColumn = nextColumn;
    }

    public void setPrevColumn(SQLColumn prevColumn) {
        if (this.prevColumn != null) this.prevColumn.nextColumn = nextColumn;
        this.prevColumn = prevColumn;
    }

    public void addData(T data){
    }

    public void removeData(T data){
    }

    private void adjustLengthBack(int amount){
        columnLength += amount;
        if (prevColumn != null) prevColumn.adjustLengthBack(amount);
    }
    private void adjustLengthForward(int amount){
        columnLength += amount;
        if (prevColumn != null) nextColumn.adjustLengthForward(amount);
    }

    public void incrementLength(){
        if (prevColumn != null) prevColumn.adjustLengthBack(1);
        if (nextColumn != null) nextColumn.adjustLengthForward(1);
        columnLength++;
    }

    public void decrementLength(){
        if (prevColumn != null) prevColumn.adjustLengthBack(-1);
        if (nextColumn != null) nextColumn.adjustLengthForward(-1);
        columnLength--;
    }

    public String getAllValues(){
        if (this.prevColumn != null) return prevColumn.getAllValues();
        StringBuilder valueString = new StringBuilder();
        getAllTitles(valueString);
        for (int i = 0; i < columnLength; i++){
            valueString.append(getDataRow(valueString, i));
        }
        return valueString.toString();
    }

    private void getAllTitles(StringBuilder infoString){
        if (nextColumn != null) {
            infoString.append(title).append(" | ");
            nextColumn.getAllTitles(infoString);
            return;
        }
        infoString.append(title);
    }

    private StringBuilder getDataRow(StringBuilder valueString, int row){
        valueString.append(data.get(row));
        if (nextColumn != null) return valueString.append(" | ").append(nextColumn.getDataRow(valueString, row));
        return valueString.append("\n");
    }

    public String getTitle() {
        return title.trim();
    }

    public int getColumnLength() {
        return columnLength;
    }

    public abstract ColumnType getType();
    public abstract void insert(T data);
}
