package io.github.gafarrell.database.column;

import java.util.ArrayList;
import java.util.List;

public class StringColumn extends SQLColumn {
    private List<String> data;
    private int size;
    private boolean var;

    public StringColumn(String title, int size) {
        super(title);
        this.data = new ArrayList<>();
        var = title.toLowerCase().contains("varchar");
        this.size = size;
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
    public void addData(String data) throws Exception {
        if (var && data.length() > size) throw new Exception("Data " + data + " invalid for column " + title);
        this.data.add(data);
    }

}
