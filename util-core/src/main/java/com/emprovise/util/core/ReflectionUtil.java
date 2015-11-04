package com.emprovise.util.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class ReflectionUtil {

    public static void setObjectField(Object object, String fieldname, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(object, value);
    }

    public static Class<?> getClassType(Field field) {
        ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
        Class<?> fieldClass = (Class<?>) fieldType.getActualTypeArguments()[0];
        return fieldClass;
    }

}
