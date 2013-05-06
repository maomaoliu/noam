package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.noam.annotation.Column;

import java.lang.reflect.Field;
import java.sql.SQLException;

public class UpsertCriteria {
    private Class klass;
    private ModelInfo modelInfo;
    private SessionFactory sessionFactory;
    private NoamMethodInterceptor methodInterceptor;
    private Object instance;

    public UpsertCriteria(Object instance, ModelInfo modelInfo, SessionFactory sessionFactory) {
        this.instance = instance;
        this.modelInfo = modelInfo;
        this.sessionFactory = sessionFactory;
        methodInterceptor = new NoamMethodInterceptor(sessionFactory);
    }

    public boolean upsert() throws SQLException {
        Integer primaryKey = FieldValueFetcher.getPrimaryKey(instance);
        String sql = null;
        if (primaryKey != null && primaryKey > 0) {
            sql = updateSql();
        } else {
            sql = insertSql();
        }
        return sessionFactory.execute(sql);
    }

    private String updateSql() {
        StringBuffer valuesBuffer = new StringBuffer();
        Field[] fields = instance.getClass().getSuperclass().getDeclaredFields();
        Integer primaryKey = FieldValueFetcher.getPrimaryKey(instance);

        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Column.class) || field.getName().equals("id")) {
                continue;
            }
            valuesBuffer.append(field.getName()).append("=");
            Object value = FieldValueFetcher.getValue(instance, field.getName());
            if (value instanceof String) {
                valuesBuffer.append("'").append(value).append("'");
            } else {
                valuesBuffer.append(value);
            }
            valuesBuffer.append(", ");
        }
        String values = valuesBuffer.substring(0, valuesBuffer.length() - 2).toString();
        return String.format("UPDATE %s SET %s WHERE id = %d", modelInfo.getTableName(), values, primaryKey);
    }

    private String insertSql() {
        StringBuffer columnsBuffer = new StringBuffer();
        StringBuffer valuesBuffer = new StringBuffer();
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Column.class) || field.getName().equals("id")) {
                continue;
            }
            columnsBuffer.append(field.getName()).append(", ");
            Object value = FieldValueFetcher.getValue(instance, field.getName());
            if (value instanceof String) {
                valuesBuffer.append("'").append(value).append("'");
            } else {
                valuesBuffer.append(value);
            }
            valuesBuffer.append(", ");
        }
        String columns = columnsBuffer.substring(0, columnsBuffer.length() - 2).toString();
        String values = valuesBuffer.substring(0, valuesBuffer.length() - 2).toString();

        return String.format("INSERT INTO %s (%s) VALUES (%s)", modelInfo.getTableName(), columns, values);
    }
}
