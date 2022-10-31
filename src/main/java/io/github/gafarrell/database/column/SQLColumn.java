package io.github.gafarrell.database.column;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLColumn {

    protected SQLColumn nextColumn = null, prevColumn = null;
    protected String title;

    public SQLColumn(String title)
    {
        this.title = title;
    }

    public void setNextColumn(SQLColumn nextColumn) {
        if (this.nextColumn != null) this.nextColumn.prevColumn = nextColumn;
        this.nextColumn = nextColumn;
    }


    public String getTitle() {
        return title.trim();
    }
    public abstract int getColumnSize();
    public abstract String getDataAtRow(int row);
    public abstract void addData(String data) throws Exception;
}
