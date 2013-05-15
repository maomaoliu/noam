package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.core.util.ModelAssembler;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Criteria<T> {

    private Class<T> klass;
    private ModelInfo modelInfo;
    private SessionFactory sessionFactory;
    private NoamMethodInterceptor methodInterceptor;
    private String predication;

    public Criteria(Class<T> klass, ModelInfo modelInfo, SessionFactory sessionFactory) {
        this.klass = klass;
        this.modelInfo = modelInfo;
        this.sessionFactory = sessionFactory;
        methodInterceptor = new NoamMethodInterceptor(sessionFactory);
    }

    public List<T> list() {
        return list(sql());
    }

    public List<T> list(String sql) {
        return listWithSuperId(sql, null).get(0);
    }

    public Map<Integer, List<T>> listWithSuperId(String sql, String superIdName) {

        Map<Integer, List<T>> map = new HashMap<>();

        try {
            queryForList(sql, superIdName, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void queryForList(String sql, String superIdName, Map<Integer, List<T>> map) throws Exception {
        ResultSet resultSet = sessionFactory.executeQuery(sql);
        while (resultSet.next()) {
            T instance = createInstance(resultSet);
            putInstanceInMap(superIdName, map, resultSet, instance);
        }
    }

    private void putInstanceInMap(String superIdName, Map<Integer, List<T>> map, ResultSet resultSet, T instance) throws Exception {
        Integer superId = 0;
        if (superIdName != null) {
            superId = (Integer) resultSet.getObject(superIdName);
        }
        List<T> list = map.get(superId);
        if (list != null) {
            list.add(instance);
        } else {
            list = createList();
            list.add(instance);
            map.put(superId, list);
        }
    }

    private List<T> createList() throws Exception {
        List<T> list;
        list = (List<T>) Enhancer.create(
                ArrayList.class,
                new Class[]{CollectionInterface.class},
                methodInterceptor);
        Method method = list.getClass().getMethod("initCollection");
        method.invoke(list);
        return list;
    }

    private T createInstance(ResultSet resultSet) throws IllegalAccessException, InstantiationException, SQLException {
        List<String> columns = modelInfo.getColumns();
        T instance = (T) Enhancer.create(klass, methodInterceptor);
        Map<String, String> parameters = new HashMap();
        for (String column : columns) {
            parameters.put(column, resultSet.getObject(column).toString());
        }

        new ModelAssembler().assembleModel(parameters, instance);
        return instance;
    }

    private String sql() {
        String sql = String.format("SELECT * FROM %s", modelInfo.getTableName());
        if (predication != null) {
            sql += " where " + predication;
        }
        return sql;
    }

    public Criteria<T> where(String predication) {
        this.predication = predication;
        return this;
    }

    public T unique() throws Exception {
        ResultSet resultSet = sessionFactory.executeQuery(sql());
        if (resultSet.next()) {
            return createInstance(resultSet);
        }
        return null;
    }

    public T getById(Integer id) {
        predication = "id = " + id;
        try {
            return unique();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
