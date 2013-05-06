package com.thoughtworks.maomao.noam;


import com.thoughtworks.maomao.core.util.NameResolver;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NoamMethodInterceptor implements MethodInterceptor {
    private SessionFactory sessionFactory;

    public NoamMethodInterceptor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean isCollection(Class klass) {
        return Arrays.asList(klass.getInterfaces()).contains(Collection.class);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Class<?> returnType = method.getReturnType();
        if (isCollection(returnType)) {
            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
            Class genericType = (Class) genericReturnType.getActualTypeArguments()[0];
            if (sessionFactory.isModel(genericType)) {
                if (returnType == List.class) {
                    String instanceName = NameResolver.getInstanceName(obj.getClass());
                    instanceName = instanceName.substring(0, instanceName.indexOf("$$"));
                    return sessionFactory.from(genericType).where(instanceName + "_id = " + FieldValueUtil.getPrimaryKey(obj)).list();
                }
                return returnType.newInstance();
            }
        }
        return proxy.invokeSuper(obj, args);
    }
}
