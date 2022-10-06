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

    public String getTitle() {
        return title;
    }
}
