package io.github.gafarrell.database.column;

import java.util.Date;

public class DatetimeColumn extends SQLColumn<Date>{
    public final ColumnType Type = ColumnType.DATE_TIME;

    public DatetimeColumn(String title) {
        super(title);
    }

    @Override
    public ColumnType getType() {
        return Type;
    }

    @Override
    public void insert(Date data) {

    }
}
