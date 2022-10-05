package io.github.gafarrell.database.data;

import java.util.HashMap;

public class ResultSet {
    private HashMap<String, Object> queryResult = new HashMap<>();

    public ResultSet(String columnName, Object data){
        queryResult.put(columnName, data);
    }

    public Object get(String column){
        return queryResult.get(column);
    }
}
