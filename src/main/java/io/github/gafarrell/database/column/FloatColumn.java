package io.github.gafarrell.database.column;

import java.util.ArrayList;
import java.util.List;

public class FloatColumn extends SQLColumn{
    private List<Float> data;
    private List<Float> queuedData = new ArrayList<>();

    public FloatColumn(String title) {
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
        try{
            float queued = Float.parseFloat(data);
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
