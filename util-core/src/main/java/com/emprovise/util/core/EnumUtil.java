package com.emprovise.util.core;

import java.lang.reflect.Method;
import java.util.*;

public class EnumUtil {

    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {

        if (value != null && !value.isEmpty()) {
            for (T e : enumClass.getEnumConstants()) {
                if (e.name().equalsIgnoreCase(value)) {
                    return Optional.of(e);
                }
            }
        }

        return Optional.empty();
    }

    public static <T extends Enum<T>> EnumSet<T> copyOf(Class<T> clazz, Collection<T> items) {
        if(items != null && !items.isEmpty()) {
            return EnumSet.copyOf(items);
        }
        return EnumSet.noneOf(clazz);
    }

    public static List<Object> getValues(Class enumClass) {
        try {
            Method method = enumClass.getMethod("values");
            return Arrays.asList((Object[]) method.invoke(enumClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getNames(Class<? extends Enum> enumClass) {

        List<String> names = new ArrayList<String>();
        List<Object> list = getValues(enumClass);

        for (Object object : list) {
            try {
                Method method = enumClass.getMethod("name");
                names.add((String) method.invoke(object));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return  names;
    }
}
