package io.github.gafarrell.database.column;

public class StringColumn extends SQLColumn<String>{

    private int size;

    public StringColumn(String title, int size) {
        super(title);
        this.size = size;
    }

    @Override
    public String getTitle() {
        return super.getTitle() + " " + size;
    }
}
