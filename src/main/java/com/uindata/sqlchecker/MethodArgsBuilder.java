package com.uindata.sqlchecker;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class MethodArgsBuilder {

    private Method method;

    public Object[] build() {
        return Arrays.stream(method.getParameters())
            .map(this::buildMethodArg)
            .collect(Collectors.toList())
            .toArray();
    }

    private Object buildMethodArg(Parameter param) {
        return buildValue(param.getParameterizedType());
    }

    private Object buildValue(Type type) {
        Object value = null;
        if (type instanceof ParameterizedType) {
            value = buildValueForCollectionTypes((ParameterizedType) type);
        } else {
            value = buildValueForNonCollectionTypes((Class<?>) type);
        }
        if (value == null) {
            throw new RuntimeException("Unsupported parameter type: " + type.getTypeName());
        }
        return value;
    }

    private Object buildValueForCollectionTypes(ParameterizedType ptype) {
        Object value = null;
        Class<?> type = (Class<?>) ptype.getRawType();
        if (type == List.class) {
            ArrayList<Object> list = new ArrayList<>();
            list.add(buildValue(ptype.getActualTypeArguments()[0]));
            value = list;
        } else if (type == Set.class) {
            HashSet<Object> set = new HashSet<>();
            set.add(buildValue(ptype.getActualTypeArguments()[0]));
            value = set;
        } else if (type == Map.class) {
            HashMap<Object, Object> map = new HashMap<>();
            map.put(buildValue(ptype.getActualTypeArguments()[0]), buildValue(ptype.getActualTypeArguments()[1]));
            value = map;
        }
        return value;
    }

    private Object buildValueForNonCollectionTypes(Class<?> type) {
        Object value = null;
        if (type.isEnum()) {
            value = buildValueForEnumTypes(type);
        }
        if (type.isArray()) {
            value = Array.newInstance(type.getComponentType(), 1);
            Array.set(value, 0, buildValue(type.getComponentType()));
        }
        if (value == null) {
            value = buildValueForPrimitiveAndWrapperTypes(type);
        }
        if (value == null) {
            value = buildValueForCoreJdkReferenceTypes(type);
        }
        if (value == null && !Modifier.isAbstract(type.getModifiers())) {
            value = buildValueForBeanTypes(type);
        }
        return value;
    }

    private Object buildValueForEnumTypes(Class<?> type) {
        return type.getEnumConstants()[0];
    }

    private Object buildValueForPrimitiveAndWrapperTypes(Class<?> type) {
        Map<Class<?>, Object> typeValueMap = new HashMap<>();
        typeValueMap.put(boolean.class, true);
        typeValueMap.put(Boolean.class, true);
        typeValueMap.put(char.class, 'c');
        typeValueMap.put(Character.class, 'c');
        typeValueMap.put(byte.class, (byte) 1);
        typeValueMap.put(Byte.class, (byte) 1);
        typeValueMap.put(short.class, (short) 1);
        typeValueMap.put(Short.class, (short) 1);
        typeValueMap.put(int.class, 1);
        typeValueMap.put(Integer.class, 1);
        typeValueMap.put(long.class, 1L);
        typeValueMap.put(Long.class, 1L);
        typeValueMap.put(float.class, 1.0F);
        typeValueMap.put(Float.class, 1.0F);
        typeValueMap.put(double.class, 1.0);
        typeValueMap.put(Double.class, 1.0);
        return typeValueMap.get(type);
    }

    private Object buildValueForCoreJdkReferenceTypes(Class<?> type) {
        Map<Class<?>, Object> typeValueMap = new HashMap<>();
        typeValueMap.put(String.class, "str");
        typeValueMap.put(Date.class, new Date());
        typeValueMap.put(BigDecimal.class, BigDecimal.valueOf(1.0));
        typeValueMap.put(BigInteger.class, BigInteger.valueOf(1L));
        return typeValueMap.get(type);
    }

    private Object buildValueForBeanTypes(Class<?> type) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(type);
        Arrays.stream(beanWrapper.getPropertyDescriptors())
            .filter(p -> p.getWriteMethod() != null)
            .forEach(p -> beanWrapper.setPropertyValue(p.getName(), buildValue(beanPropertyType(p.getWriteMethod()))));
        return beanWrapper.getWrappedInstance();
    }

    private Type beanPropertyType(Method setter) {
        return setter.getParameters()[0].getParameterizedType();
    }

}
