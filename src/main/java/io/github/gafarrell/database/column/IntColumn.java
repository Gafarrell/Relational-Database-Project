package io.github.gafarrell.database.column;

import io.github.gafarrell.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntColumn extends SQLColumn{
    private List<Integer> queuedData = new ArrayList<>();

    public IntColumn(String title) {
        super(title);
        data = new ArrayList<>();
    }

    @Override
    public SQLColumn clone() {
        IntColumn copy = new IntColumn(title);
        return copy;
    }

    public SQLColumn deepClone() {
        IntColumn copy = new IntColumn(title);
        copy.data = new ArrayList<>(data);
        return copy;
    }

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
        Debug.writeLine(data);
        try{
            if (data == null) queuedData.add(0);
            else {
                int queued = Integer.parseInt(data);
                return queuedData.add(queued);
            }
        }
        catch (Exception ignore){}
        return false;
    }

    @Override
    public int[] select(String operator, String value) {
        List<Integer> selection = new ArrayList<Integer>();
        try {
            int comparison = Integer.parseInt(value);

            for (int i = 0; i < data.size(); i++) {
                Debug.writeLine("Comparing " + data.get(i) + " with " + comparison + " using " + operator);
                switch (operator) {
                    case "=" ->  { if ((Integer) data.get(i) == comparison) selection.add(i); }
                    case "<=" -> { if ((Integer) data.get(i) <= comparison) selection.add(i); }
                    case ">=" -> { if ((Integer) data.get(i) >= comparison) selection.add(i); }
                    case "<" ->  { if ((Integer) data.get(i) < comparison)  selection.add(i); }
                    case ">" ->  { if ((Integer) data.get(i) > comparison)  selection.add(i); }
                    default -> { return null; }
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
        if (!(otherColumn instanceof IntColumn castedCol)) return null;

        HashMap<Integer, List<Integer>> items = new HashMap<>();

        List<Object> otherData = castedCol.data;

        for (int i = 0; i < data.size(); i++){
            items.put(i, new ArrayList<>());
            for (int j = 0; j < otherData.size(); j++){
                switch (operator) {
                    case "=" ->  { if (data.get(i).equals(otherData.get(j))) items.get(i).add(j); }
                    case "<=" -> { if ((Integer) data.get(i) <= (Integer) otherData.get(j)) items.get(i).add(j); }
                    case ">=" -> { if ((Integer) data.get(i) >= (Integer) otherData.get(j)) items.get(i).add(j); }
                    case "<" ->  { if ((Integer) data.get(i) < (Integer) otherData.get(j))  items.get(i).add(j); }
                    case ">" ->  { if ((Integer) data.get(i) > (Integer) otherData.get(j))  items.get(i).add(j); }
                    default -> { return null; }
                }
            }
        }

        return items;
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

    @Override
    public void setDataAtRow(int row, String data) {
        int dataParsed = Integer.parseInt(data);
        this.data.set(row, dataParsed);
    }

    @Override
    public void deleteDataAt(int row) {
        data.remove(row);
    }

    @Override
    public Type getType() {
        return Type.INT;
    }
}
