package com.sanxs.matcher;

import com.sanxs.matcher.function.GroupMatchFunction;
import com.sanxs.matcher.function.gorup.AbstractGroupByAggregateHandler;
import lombok.Getter;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description:
 */
@Getter
public class GroupBy<Data, GroupData extends Data> {

    /**
     * 需要进行group的字段列表
     */
    private final List<GroupMatchFunction<Data, ?>> groupMatchFunctions;
    private final List<AbstractGroupByAggregateHandler<Data, GroupData, ?, ?>> aggregateHandlers;
    private final Class<GroupData> groupDataClass;
    private final Where<GroupData> having;

    public GroupBy(Class<GroupData> clazz) {
        this.groupMatchFunctions = new LinkedList<>();
        this.aggregateHandlers = new LinkedList<>();
        this.groupDataClass = clazz;
        this.having = new Where<>();
    }

    public GroupBy<Data, GroupData> appendKey(GroupMatchFunction<Data, ?> groupByKey) {
        this.groupMatchFunctions.add(groupByKey);
        return this;
    }

    public GroupBy<Data, GroupData> appendAggregate(AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> groupByKey) {
        this.aggregateHandlers.add(groupByKey);
        return this;
    }


    @Getter
    @SuppressWarnings("unchecked")
    public static class GroupKey<Data> {
        private final String key;
        private final Data objectKey;

        public GroupKey(Collection<GroupMatchFunction<Data, ?>> groupByKeysOperation, Data data) {
            StringBuilder keysBuilder = new StringBuilder();
            try {
                this.objectKey = (Data) data.getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Requires no-parameter construction method " + data.getClass().getName());
            }
            for (Function<Data, ?> cache : groupByKeysOperation) {
                keysBuilder.append(getFieldNameAndValue(cache, data, this.objectKey));
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
            GroupKey<Data> groupKey = (GroupKey<Data>) o;
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
     * @param source             数据对象
     * @return group key [methodName:<value>]
     */
    static <T> String getFieldNameAndValue(Function<T, ?> groupMatchFunction, T source, T target) {
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
            value = field.get(source);
            field.set(target, value);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return String.format("[%s:%s]", field.getName(), value != null ? "<" + value.toString() + ">" : null);
    }

}
