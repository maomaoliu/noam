package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.noam.annotation.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModelInfo {

    private String tableName;
    private List<String> columns = new ArrayList<String>();

    public ModelInfo(Class modelClass) {
        tableName = modelClass.getSimpleName().toUpperCase();
        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                columns.add(field.getName());
            }
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }
}
