package io.github.gafarrell.database.column;

import java.util.ArrayList;
import java.util.List;

public class IntColumn extends SQLColumn{
    private List<Integer> data;

    public IntColumn(String title) {
        super(title);
        data = new ArrayList<>();
    }

    @Override
    public int getColumnSize() {
        return data.size();
    }

    @Override
    public String getDataAtRow(int row) {
        return data.get(row).toString();
    }

    @Override
    public void addData(String data) {

    }
}
