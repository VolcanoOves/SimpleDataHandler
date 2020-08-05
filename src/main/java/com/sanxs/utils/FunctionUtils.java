package com.sanxs.utils;

import com.sanxs.matcher.function.gorup.AggregateFunction;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: lambda function utils
 **/
public class FunctionUtils {

    public static final String GET = "get";
    public static final String SET = "set";
    public static final String IS = "is";

    /**
     * 获取function对应的值
     *
     * @param groupMatchFunction function
     * @param data               数据对象
     */
    public static <T> Object getFieldValue(Serializable groupMatchFunction, T data) {

        SerializedLambda serializedLambda = getSerializedLambda(groupMatchFunction);
        String fieldName = getFieldName(serializedLambda);

        Field field;
        Object value;

        try {
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
            field.setAccessible(true);
            value = field.get(data);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return value;
    }

    /**
     * 设置function对应对象的值
     *
     * @param groupMatchFunction function
     * @param data               数据对象
     */
    public static <T, V> void setFieldValue(AggregateFunction<T, V> groupMatchFunction, T data, V value) {
        SerializedLambda serializedLambda = getSerializedLambda(groupMatchFunction);
        String fieldName = getFieldName(serializedLambda);

        Field field;

        try {
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(data, value);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取字段名称
     *
     * @param groupMatchFunction function
     * @return 字段名
     */
    public static String getClassFieldName(Serializable groupMatchFunction) {
        SerializedLambda serializedLambda = getSerializedLambda(groupMatchFunction);
        String fieldName = getFieldName(serializedLambda);

        String className = serializedLambda.getImplClass().replace("/", ".");
        return className + "#" + fieldName;
    }

    /**
     * lambda 方式取得该对象的方法名以及字段值组成一个key
     * 用于构造group key 专用方法
     *
     * @param groupMatchFunction function
     * @param source             数据对象
     * @return group key [methodName:<value>]
     */
    public static <T> String getFieldNameAndValue(Object groupMatchFunction, T source, T target) {
        SerializedLambda serializedLambda = getSerializedLambda(groupMatchFunction);
        String fieldName = getFieldName(serializedLambda);

        Field field;
        Object value;

        try {
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
            field.setAccessible(true);
            value = field.get(source);
            field.set(target, value);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return String.format("[%s:%s]", field.getName(), value != null ? "<" + value.toString() + ">" : null);
    }

    private static SerializedLambda getSerializedLambda(Object function) {
        // 从function取出序列化方法
        Method writeReplaceMethod;
        try {
            writeReplaceMethod = function.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        writeReplaceMethod.setAccessible(true);

        try {
            return (SerializedLambda) writeReplaceMethod.invoke(function);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取方法名
     *
     * @param serializedLambda serializedLambda
     * @return fieldName
     */
    private static String getFieldName(SerializedLambda serializedLambda) {
        // 从lambda信息取出method、field、class等
        // 此段算法只适合标准驼峰命名的字段，由于时间限制未做更深入的优化
        String fieldName = serializedLambda.getImplMethodName();
        if (fieldName.startsWith(GET) || fieldName.startsWith(SET)) {
            fieldName = fieldName.substring(3);
        } else if (fieldName.startsWith(IS)) {
            fieldName = fieldName.substring(2);
        }

        return fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
    }
}
