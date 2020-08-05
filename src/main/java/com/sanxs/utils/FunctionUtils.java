package com.sanxs.utils;

import com.sanxs.matcher.function.gorup.AggregateFunction;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: lambda function utils
 **/
public class FunctionUtils {
    /**
     * lambda 方式取得该对象的方法名以及字段值组成一个key
     *
     * @param groupMatchFunction function
     * @param data               数据对象
     */
    public static <T> Object getFieldValue(Function<T, ?> groupMatchFunction, T data) {
        // 从function取出序列化方法
        Method writeReplaceMethod;
        try {
            writeReplaceMethod = groupMatchFunction.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(groupMatchFunction);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);

        // 从lambda信息取出method、field、class等
        // 此段算法只适合标准驼峰命名的字段，由于时间限制未做更深入的优化
        String fieldName = serializedLambda.getImplMethodName().startsWith("get") ?
                serializedLambda.getImplMethodName().substring("get".length())
                : serializedLambda.getImplMethodName().substring("is".length());

        fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());

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
     * lambda 方式取得该对象的方法名以及字段值组成一个key
     *
     * @param groupMatchFunction function
     * @param data               数据对象
     */
    public static <T, V> void setFieldValue(AggregateFunction<T, V> groupMatchFunction, T data, V value) {
        // 从function取出序列化方法
        Method writeReplaceMethod;

        try {
            writeReplaceMethod = groupMatchFunction.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(groupMatchFunction);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);

        // 从lambda信息取出method、field、class等
        // 此段算法只适合标准驼峰命名的字段，由于时间限制未做更深入的优化
        String fieldName = serializedLambda.getImplMethodName().startsWith("get") ?
                serializedLambda.getImplMethodName().substring("get".length() + 1)
                : serializedLambda.getImplMethodName().substring("is".length() + 1);

        fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());

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
     * @param groupMatchFunction
     * @return
     */
    public static String getMethodName(Serializable groupMatchFunction) {
        // 从function取出序列化方法
        Method writeReplaceMethod;

        try {
            writeReplaceMethod = groupMatchFunction.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(groupMatchFunction);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);
        return serializedLambda.getImplMethodName();
    }

}
