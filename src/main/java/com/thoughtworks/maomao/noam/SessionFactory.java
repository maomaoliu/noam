package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.container.WheelContainer;
import com.thoughtworks.maomao.noam.annotation.Model;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SessionFactory {

    private final Set<Class> modelClasses;
    private final Map<Class, ModelInfo> modelInfoMap = new HashMap();
    private final Connection connection;

    public SessionFactory(String packageName, String driver, String ConnectionUrl) {
        WheelContainer wheelContainer = new WheelContainer(packageName, new Class[]{Model.class});
        modelClasses = wheelContainer.getAllClassWithAnnotation(Model.class);
        for (Class modelClass : modelClasses) {
            modelInfoMap.put(modelClass, new ModelInfo(modelClass));
        }
        connection = getConnection(driver, ConnectionUrl);
    }

    public <T> Criteria<T> from(Class<T> klass) {
        if (!modelClasses.contains(klass)) {
            throw new RuntimeException("not a model class");
        }
        return new Criteria(klass, modelInfoMap.get(klass), this);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    public boolean isModel(Class klass) {
        return modelClasses.contains(klass);
    }

    public boolean save(Object object) {
        return save(object, null);
    }

    public Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    public boolean save(Object object, Map<String, Object> extraParameters) {
        Class<?> klass = object.getClass();
        checkModel(klass);
        ModelInfo modelInfo = getModelInfo(klass);
        try {
            return new UpsertCriteria(object, modelInfo, this).withExtraParameter(extraParameters).upsert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int delete(Object object) {
        Class<?> klass = object.getClass();
        checkModel(klass);
        ModelInfo modelInfo = getModelInfo(klass);
        try {
            return new DeleteCriteria(object, modelInfo, this).delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void checkModel(Class<?> klass) {
        if (!modelClasses.contains(klass) && !modelClasses.contains(klass.getSuperclass())) {
            throw new RuntimeException("not a model class");
        }
    }

    private ModelInfo getModelInfo(Class<?> klass) {
        ModelInfo modelInfo = modelInfoMap.get(klass);
        if (modelInfo == null) {
            modelInfo = modelInfoMap.get(klass.getSuperclass());
        }
        return modelInfo;
    }

    private Connection getConnection(String driver, String ConnectionUrl) {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(ConnectionUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
