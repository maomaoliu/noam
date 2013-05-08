package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.core.util.ModelAssembler;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationTargetException;
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

    public List<T> list() throws SQLException {
        return list(sql());
    }

    public List<T> list(String sql) throws SQLException {
        return listWithSuperId(sql, null).get(0);
    }

    public Map<Integer,List<T>> listWithSuperId(String sql, String superIdName) throws SQLException {

        Map<Integer,List<T>> map = new HashMap<>();

        ResultSet resultSet = sessionFactory.executeQuery(sql);
        try {
            while (resultSet.next()) {
                T instance = createInstance(resultSet);
                Integer superId = 0;
                if(superIdName != null){
                    superId = (Integer) resultSet.getObject(superIdName);
                }
                List<T> list = map.get(superId);
                if(list != null){
                    list.add(instance);
                } else {
                    list = createList();
                    list.add(instance);
                    map.put(superId, list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private List<T> createList() {
        List<T> list;
        list = (List<T>) Enhancer.create(
                ArrayList.class,
                new Class[]{CollectionInterface.class},
                methodInterceptor);
        try {
            Method method = list.getClass().getMethod("initCollection");
            method.invoke(list);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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

    public T unique() throws SQLException {
        ResultSet resultSet = sessionFactory.executeQuery(sql());
        try {
            if (resultSet.next()) {
                return createInstance(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
