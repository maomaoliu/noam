package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.core.util.ModelAssembler;
import com.thoughtworks.maomao.core.util.NameResolver;

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

    public Criteria(Class<T> klass, ModelInfo modelInfo, SessionFactory sessionFactory) {
        this.klass = klass;
        this.modelInfo = modelInfo;
        this.sessionFactory = sessionFactory;
    }

    public List<T> list() throws SQLException {
        List<T> result = new ArrayList<T>();
        ResultSet resultSet = sessionFactory.executeQuery(sql());
        try {
            while (resultSet.next()) {
                result.add(createInstance(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private T createInstance(ResultSet resultSet) throws IllegalAccessException, InstantiationException, SQLException {
        List<String> columns = modelInfo.getColumns();
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        for (String column : columns) {
            parameters.put(NameResolver.getInstanceName(klass) + "." + column, new String[]{resultSet.getObject(column).toString()});
        }
        return new ModelAssembler().assembleModel(parameters, klass);
    }

    private String sql() {
        return String.format("SELECT * FROM %s", modelInfo.getTableName());
    }
}
