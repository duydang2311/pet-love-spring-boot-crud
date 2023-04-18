package com.stc.petlove.utils;

import lombok.val;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Stream;

public final class PropertyUtils {
    private PropertyUtils() { }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .filter(propertyName -> {
                    val value = wrappedSource.getPropertyValue(propertyName);
                    if (value != null) {
                        LogManager.getLogManager().getLogger("").log(Level.INFO, value.toString());
                    }
                    return value == null || (value instanceof List && ((List<?>)value).size() == 0);
                }).toArray(String[]::new);
    }
}
