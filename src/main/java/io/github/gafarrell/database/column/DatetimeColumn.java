package io.github.gafarrell.database.column;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatetimeColumn extends SQLColumn {
    private List<Date> data;

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
    public void addData(String data) {

    }
}
