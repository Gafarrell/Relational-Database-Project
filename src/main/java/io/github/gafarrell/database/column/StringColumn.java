package io.github.gafarrell.database.column;

public class StringColumn extends SQLColumn<String>{
    public final ColumnType Type = ColumnType.STRING;

    private int size;
    private boolean var;

    public StringColumn(String title, int size) {
        super(title);
        var = title.toLowerCase().contains("varchar");
        this.size = size;
    }

    @Override
    public ColumnType getType() {
        return Type;
    }

    @Override
    public void insert(String data) {

    }
}
