package com.thoughtworks.maomao.noam;

import java.lang.reflect.Method;

import static com.google.common.base.CaseFormat.*;

public class FieldValueUtil {
    public static Integer getPrimaryKey(Object instance) {
        return (Integer) getValue(instance, "id");
    }

    public static void setPrimaryKey(Object instance, Integer value) {
        try {
            Method method = instance.getClass().getMethod("setId", Integer.class);
            method.invoke(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getValue(Object instance, String fieldName) {
        try {
            Method method = instance.getClass().getMethod(String.format("get%s", toLowerCamel(fieldName)));
            return method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toLowerCamel(String fieldName) {
        return LOWER_CAMEL.to(UPPER_CAMEL, fieldName);
    }

    public static String toPlural(String name) {
        if (name.endsWith("s")) {
            return name + "es";
        } else {
            return name + "s";
        }
    }

    public static String toUpperUnderscore(String name) {
        return UPPER_CAMEL.to(UPPER_UNDERSCORE, name);
    }
}
