package io.github.gafarrell.database.column;

public class StringColumn extends SQLColumn<String>{

    private int size;
    private boolean var;

    public StringColumn(String title, int size) {
        super(title);
        var = title.toLowerCase().contains("varchar");
        this.size = size;
    }



}
