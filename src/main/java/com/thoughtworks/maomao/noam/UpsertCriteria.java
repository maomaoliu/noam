package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.noam.annotation.Column;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class UpsertCriteria {
    private ModelInfo modelInfo;
    private SessionFactory sessionFactory;
    private Object instance;
    private Map<String, Object> extraParameters = new HashMap<>();

    public UpsertCriteria(Object instance, ModelInfo modelInfo, SessionFactory sessionFactory) {
        this.instance = instance;
        this.modelInfo = modelInfo;
        this.sessionFactory = sessionFactory;
    }

    public boolean upsert() throws Exception {
        Integer primaryKey = FieldValueUtil.getPrimaryKey(instance);
        String sql;
        if (primaryKey != null && primaryKey > 0) {
            sql = updateSql();
        } else {
            sql = insertSql();
        }
        Statement statement = sessionFactory.getStatement();
        boolean result = statement.execute(sql);
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            FieldValueUtil.setPrimaryKey(instance, generatedKeys.getInt(1));
        }
        upsertAssociation();
        return result;
    }

    private void upsertAssociation() throws Exception {
        Map<String, Object> extraParameters = new HashMap<>();
        extraParameters.put(modelInfo.getTableName() + "_id", FieldValueUtil.getPrimaryKey(instance));
        Field[] fields = modelInfo.getModelClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> returnType = field.getType();
            if (Arrays.asList(returnType.getInterfaces()).contains(Collection.class)) {
                ParameterizedType genericReturnType = (ParameterizedType) field.getGenericType();
                Class genericType = (Class) genericReturnType.getActualTypeArguments()[0];
                if (sessionFactory.isModel(genericType)) {
                    Collection associations = (Collection) FieldValueUtil.getValue(instance, field.getName());
                    for (Object association : associations) {
                        sessionFactory.save(association, extraParameters);
                    }
                }
            }
        }

    }

    private String updateSql() {
        StringBuffer valuesBuffer = new StringBuffer();
        Field[] fields = modelInfo.getModelClass().getDeclaredFields();
        Integer primaryKey = FieldValueUtil.getPrimaryKey(instance);

        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Column.class) || field.getName().equals("id")) {
                continue;
            }
            Object value = FieldValueUtil.getValue(instance, field.getName());
            append(valuesBuffer, field.getName(), value);
        }
        Set<Map.Entry<String,Object>> entries = extraParameters.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            append(valuesBuffer, entry.getKey(), entry.getValue());
        }
        String values = valuesBuffer.substring(0, valuesBuffer.length() - 2).toString();
        return String.format("UPDATE %s SET %s WHERE id = %d", modelInfo.getTableName(), values, primaryKey);
    }

    private void append(StringBuffer buffer, String name, Object value) {
        buffer.append(name).append("=");
        appendValue(buffer, value);
    }

    private void appendValue(StringBuffer buffer, Object value) {
        if (value instanceof String) {
            buffer.append("'").append(value).append("'");
        } else {
            buffer.append(value);
        }
        buffer.append(", ");
    }

    private String insertSql() {
        StringBuffer columnsBuffer = new StringBuffer();
        StringBuffer valuesBuffer = new StringBuffer();
        Field[] fields = modelInfo.getModelClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Column.class) || field.getName().equals("id")) {
                continue;
            }
            columnsBuffer.append(field.getName()).append(", ");
            Object value = FieldValueUtil.getValue(instance, field.getName());
            appendValue(valuesBuffer, value);
        }

        Set<Map.Entry<String,Object>> entries = extraParameters.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            columnsBuffer.append(entry.getKey()).append(", ");
            appendValue(valuesBuffer, entry.getValue());
        }

        String columns = columnsBuffer.substring(0, columnsBuffer.length() - 2).toString();
        String values = valuesBuffer.substring(0, valuesBuffer.length() - 2).toString();

        return String.format("INSERT INTO %s (%s) VALUES (%s)", modelInfo.getTableName(), columns, values);
    }

    public UpsertCriteria withExtraParameter(Map<String, Object> extraParameters) {
        if (extraParameters != null) {
            this.extraParameters = extraParameters;
        }
        return this;
    }
}
