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

    public SessionFactory(String packageName) {
        WheelContainer wheelContainer = new WheelContainer(packageName, new Class[]{Model.class});
        modelClasses = wheelContainer.getAllClassWithAnnotation(Model.class);
        for (Class modelClass : modelClasses) {
            modelInfoMap.put(modelClass, new ModelInfo(modelClass));
        }
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public boolean execute(String sql) throws SQLException {
        return connection.createStatement().execute(sql);
    }

    public boolean isModel(Class klass) {
        return modelClasses.contains(klass);
    }

    public boolean save(Object object) throws SQLException {
        Class<?> klass = object.getClass();
        if (!modelClasses.contains(klass) && !modelClasses.contains(klass.getSuperclass())) {
            throw new RuntimeException("not a model class");
        }
        ModelInfo modelInfo = modelInfoMap.get(klass);
        if (modelInfo == null) {
            modelInfo = modelInfoMap.get(klass.getSuperclass());
        }
        return new UpsertCriteria(object, modelInfo, this).upsert();
    }
}
