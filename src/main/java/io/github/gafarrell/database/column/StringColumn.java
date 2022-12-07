package io.github.gafarrell.database.column;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringColumn extends SQLColumn {
    private List<Object> data = new ArrayList<>();
    private final List<String> dataQueue = new ArrayList<>();
    private final int size;
    private final boolean var;

    public StringColumn(String title, int size) {
        super(title);
        var = title.toLowerCase().contains("varchar");
        this.size = size;
    }

    @Override
    public SQLColumn clone() {
        return new StringColumn(title, size);
    }

    public SQLColumn deepClone() {
        StringColumn copy = new StringColumn(title, size);
        copy.data = new ArrayList<>(data);
        return copy;
    }

    @Override
    public List<Object> getData(){return data;}

    @Override
    public int getColumnSize() {
        return data.size();
    }

    @Override
    public String getDataAtRow(int row) {
        return row > data.size() ? null : data.get(row).toString();
    }

    @Override
    public boolean queueData(String data) {
        return dataQueue.add(data == null ? "" : data);
    }

    @Override
    public int[] select(String operator, String value) {
        List<Integer> selection = new ArrayList<Integer>();
        try {

            for (int i = 0; i < data.size(); i++) {
                if ("=".equals(operator)) {
                    if (data.get(i).equals(value)) selection.add(i);
                } else {
                    return null;
                }
            }
        }
        catch (Exception e){
            return null;
        }

        int[] arr = new int[selection.size()];
        for (int i = 0; i < selection.size(); i++) arr[i] = selection.get(i);

        return arr;
    }

    @Override
    public Map<Integer, List<Integer>> selectWhere(String operator, SQLColumn otherColumn) {
        if (!(otherColumn instanceof StringColumn castedCol)) return null;

        HashMap<Integer, List<Integer>> items = new HashMap<>();

        List<Object> otherData = castedCol.data;

        for (int i = 0; i < data.size(); i++){
            items.put(i, new ArrayList<>());
            for (int j = 0; j < otherData.size(); j++){
                if (data.get(i).equals(otherData.get(j))){
                    items.get(i).add(j);
                }
            }
        }

        return items;
    }

    @Override
    public void clearQueue() {
        dataQueue.clear();
    }

    @Override
    public void insertQueue() {
        data.addAll(dataQueue);
        clearQueue();
    }

    @Override
    public void setDataAtRow(int row, String data) {
        this.data.set(row, data);
    }

    @Override
    public void deleteDataAt(int row) {
        data.remove(row);
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }
}
