package io.github.gafarrell.database.column;

public class IntColumn extends SQLColumn<Integer>{
    public final ColumnType Type = ColumnType.INT;
    public IntColumn(String title) {
        super(title);
    }

    @Override
    public ColumnType getType() {
        return Type;
    }

    @Override
    public void insert(Integer data) {

    }
}
