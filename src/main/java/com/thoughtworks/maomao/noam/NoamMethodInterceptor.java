package com.thoughtworks.maomao.noam;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class NoamMethodInterceptor implements MethodInterceptor {
    private SessionFactory sessionFactory;

    private boolean isList = false;
    private boolean hasUsedIterator = false;
    private List<Integer> ids = new ArrayList<>();
    private List modelList;
    private List<Method> subModelList = new ArrayList<>();

    public NoamMethodInterceptor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean isCollection(Class klass) {
        return Arrays.asList(klass.getInterfaces()).contains(Collection.class);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("initCollection")) {
            this.isList = true;
            return null;
        }
        if (method.getName().equals("iterator")) {
            this.hasUsedIterator = true;
            this.modelList = (List) obj;
            getIds(modelList);
            return proxy.invokeSuper(obj, args);
        }
        Class<?> returnType = method.getReturnType();
        if (isCollection(returnType)) {
            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
            Class genericType = (Class) genericReturnType.getActualTypeArguments()[0];
            if (sessionFactory.isModel(genericType)) {
                if (returnType == List.class) {
                    String modelTable = new ModelInfo(obj.getClass()).getTableName();
                    modelTable = modelTable.substring(0, modelTable.indexOf("$$"));
                    String subModelTable = new ModelInfo(genericType).getTableName();
                    if (isList && hasUsedIterator) {
                        if (subModelList.contains(method)) {
                            return proxy.invokeSuper(obj, args);
                        } else {
                            String sql = String.format(
                                    "SELECT SUB.* FROM %s AS MAIN JOIN %s AS SUB ON MAIN.ID=SUB.%s_ID WHERE MAIN.ID in (%s)",
                                    modelTable, subModelTable, modelTable, assembleId(ids));
                            Map map = sessionFactory.from(genericType).listWithSuperId(sql, modelTable + "_ID");
                            updateModel(map, FieldValueUtil.toPlural(FieldValueUtil.toLowerCamel(subModelTable.toLowerCase())));
                            subModelList.add(method);

                            return proxy.invokeSuper(obj, args);
                        }
                    }

                    return sessionFactory.from(genericType).where(modelTable + "_id = " + FieldValueUtil.getPrimaryKey(obj)).list();
                }
                return returnType.newInstance();
            }
        }
        return proxy.invokeSuper(obj, args);
    }

    private void updateModel(Map map, String fieldName) {
        for(int i = 0; i < modelList.size(); i++){
            Object model = modelList.get(i);
            try {
                Method method = model.getClass().getMethod(String.format("set%s", LOWER_CAMEL.to(UPPER_CAMEL, fieldName)), List.class);
                Object o = map.get(FieldValueUtil.getPrimaryKey(model));
                method.invoke(model, o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String assembleId(List<Integer> ids) {
        String idsString = ids.toString();
        return idsString.substring(1, idsString.length() - 1);
    }


    private void getIds(List list) {
        for (int i = 0; i < list.size(); i++) {
            ids.add(getPrimaryKey(list.get(i)));
        }
    }

    private Integer getPrimaryKey(Object instance) {
        return FieldValueUtil.getPrimaryKey(instance);
    }


}
