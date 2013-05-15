package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.noam.annotation.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModelInfo {

    private String tableName;
    private List<String> columns = new ArrayList();
    private Class modelClass;

    public ModelInfo(Class modelClass) {
        this.modelClass = modelClass;
        this.tableName = FieldValueUtil.toUpperUnderscore(modelClass.getSimpleName());
        initColumns(modelClass);
    }

    private void initColumns(Class modelClass) {
        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                this.columns.add(field.getName());
            }
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public Class getModelClass() {
        return modelClass;
    }
}
