package io.github.gafarrell.database.column;

public class FloatColumn extends SQLColumn<Float>{
    public final ColumnType Type = ColumnType.FLOAT;
    public FloatColumn(String title) {
        super(title);
    }

    @Override
    public ColumnType getType() {
        return Type;
    }

    @Override
    public void insert(Float data) {

    }
}
