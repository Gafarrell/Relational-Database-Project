package io.github.gafarrell.database.column;

import java.util.ArrayList;

public abstract class SQLColumn<T> {
    private SQLColumn nextColumn, prevColumn;
    private String title;
    private final ArrayList<T> data = new ArrayList<>();

    public SQLColumn(String title)
    {
        this.title = title;
    }

    public void Insert(T data)
    {

    }

    public void attachColumn(SQLColumn nextColumn, SQLColumn prevColumn){
        nextColumn.prevColumn = this;
        prevColumn.nextColumn = this;
    }


}
