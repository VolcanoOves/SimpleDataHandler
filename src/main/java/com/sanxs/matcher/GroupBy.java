package com.sanxs.matcher;

import com.sanxs.matcher.function.GroupMatchFunction;
import lombok.Getter;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description:
 */
@Getter
public class GroupBy<T> {
    private final Map<GroupKey<T>, List<T>> data;
    private final List<GroupMatchFunction<T, ?>> groupMatchFunctions;

    public GroupBy() {
        this.data = new ConcurrentHashMap<>();
        this.groupMatchFunctions = new LinkedList<>();
    }

    public GroupBy<T> append(GroupMatchFunction<T, ?> groupByKey) {
        this.groupMatchFunctions.add(groupByKey);
        return this;
    }

    @Getter
    public static class GroupKey<T> {
        private String key;

        public GroupKey(Collection<GroupMatchFunction<T, ?>> groupByKeysOperation, T data) {
            StringBuilder keysBuilder = new StringBuilder();
            for (GroupMatchFunction<T, ?> cache : groupByKeysOperation) {
                keysBuilder.append(getFieldNameAndValue(cache, data));
            }
            this.key = keysBuilder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GroupKey groupKey = (GroupKey) o;
            return Objects.equals(key, groupKey.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    /**
     * lambda 方式取得该对象的方法名以及字段值组成一个key
     *
     * @param groupMatchFunction function
     * @param data               数据对象
     * @return group key [methodName:<value>]
     */
    static <T> String getFieldNameAndValue(GroupMatchFunction<T, ?> groupMatchFunction, T data) {
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

        return String.format("[%s:%s]", field.getName(), value != null ? "<" + value.toString() + ">" : null);
    }


}
