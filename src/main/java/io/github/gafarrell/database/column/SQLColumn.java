package io.github.gafarrell.database.column;

import io.github.gafarrell.Debug;

import java.util.List;
import java.util.Map;

public abstract class SQLColumn {
    public enum Type{
        FLOAT, INT, STRING;
    }

    protected SQLColumn nextColumn = null, prevColumn = null;
    protected String title;
    protected List<Object> data;

    public SQLColumn(String title)
    {
        this.title = title;
        Debug.writeLine("Created sql column with title: " + title);
    }

    public List<Object> getData() {return data;}

    public abstract SQLColumn clone();

    public String getTitle() {
        return title.trim();
    }
    public abstract int getColumnSize();
    public abstract String getDataAtRow(int row);
    public abstract boolean queueData(String data);
    public abstract int[] select(String operator, String value);
    public abstract void clearQueue();
    public abstract void insertQueue();
    public abstract void setDataAtRow(int row, String data);
    public abstract void deleteDataAt(int row);
    public abstract Type getType();
}
