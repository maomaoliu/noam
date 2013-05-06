package com.thoughtworks.maomao.noam;

import java.lang.reflect.Method;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class FieldValueFetcher {
    public static Integer getPrimaryKey(Object instance) {
        return (Integer) getValue(instance, "id");
    }

    public static Object getValue(Object instance, String fieldName) {
        try {
            Method method = instance.getClass().getMethod(String.format("get%s", LOWER_CAMEL.to(UPPER_CAMEL, fieldName)));
            return method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
