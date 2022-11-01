package io.github.gafarrell.database.column;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatetimeColumn extends SQLColumn {
    private List<Date> data;
    private List<Date> queuedData;

    public DatetimeColumn(String title) {
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
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = formatter.parse(data);
            return queuedData.add(date);
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
