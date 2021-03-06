package com.thoughtworks.maomao.noam;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

public class DeleteCriteria {

    private final Object instance;
    private final ModelInfo modelInfo;
    private final SessionFactory sessionFactory;

    public DeleteCriteria(Object instance, ModelInfo modelInfo, SessionFactory sessionFactory) {
        this.instance = instance;
        this.modelInfo = modelInfo;
        this.sessionFactory = sessionFactory;
    }

    public int delete() throws SQLException {
        deleteAssociations();
        Statement statement = sessionFactory.getStatement();
        return statement.executeUpdate(deleteSql());
    }

    private void deleteAssociations() {
        Field[] fields = modelInfo.getModelClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> returnType = field.getType();
            if (Arrays.asList(returnType.getInterfaces()).contains(Collection.class)) {
                deleteAssociation(field);
            }
        }
    }

    private void deleteAssociation(Field field) {
        ParameterizedType genericReturnType = (ParameterizedType) field.getGenericType();
        Class genericType = (Class) genericReturnType.getActualTypeArguments()[0];
        if (sessionFactory.isModel(genericType)) {
            Collection associations = (Collection) FieldValueUtil.getValue(instance, field.getName());
            if (associations == null) return;
            for (Object association : associations) {
                sessionFactory.delete(association);
            }
        }
    }

    private String deleteSql() {
        Integer primaryKey = FieldValueUtil.getPrimaryKey(instance);
        return String.format("DELETE FROM %s WHERE ID = %s", modelInfo.getTableName(), primaryKey);
    }
}
