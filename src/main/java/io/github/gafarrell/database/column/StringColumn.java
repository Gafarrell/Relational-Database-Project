package io.github.gafarrell.database.column;

import java.util.ArrayList;
import java.util.List;

public class StringColumn extends SQLColumn {
    private List<String> data;
    private List<String> dataQueue;
    private int size;
    private boolean var;

    public StringColumn(String title, int size) {
        super(title);
        this.data = new ArrayList<>();
        this.dataQueue = new ArrayList<>();
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
    public boolean queueData(String data) {
        return dataQueue.add(data);
    }

    @Override
    public void clearQueue() {
        dataQueue.clear();
    }

    @Override
    public void insertQueue() {
        data.addAll(dataQueue);
    }
}
