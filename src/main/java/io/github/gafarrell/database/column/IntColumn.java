package io.github.gafarrell.database.column;

import io.github.gafarrell.Debug;

import java.util.ArrayList;
import java.util.List;

public class IntColumn extends SQLColumn{
    private List<Integer> data;
    private List<Integer> queuedData = new ArrayList<>();

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
    public boolean queueData(String data) {
        Debug.writeLine(data);
        try{
            int queued = Integer.parseInt(data);
            return queuedData.add(queued);
        }
        catch (Exception ignore){}
        return false;
    }

    @Override
    public void clearQueue() {
        queuedData.clear();
    }

    @Override
    public void insertQueue() {
        data.addAll(queuedData);
        queuedData.clear();
    }
}
