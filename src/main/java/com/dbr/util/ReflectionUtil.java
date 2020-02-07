package com.dbr.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {

    public static <T, S> S invokeGetter(Class<S> returnClazz, T object, Field field) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        BeanInfo info = Introspector.getBeanInfo(object.getClass(), Object.class);
        PropertyDescriptor[] props = info.getPropertyDescriptors();
        S retval = null;
        for (PropertyDescriptor pd : props) {
            Method getter = pd.getReadMethod();
            if (("get" + field.getName()).toLowerCase().equals(getter.getName().toLowerCase())) {
                retval = (S) getter.invoke(object);
            }
        }
        return retval;
    }

}
