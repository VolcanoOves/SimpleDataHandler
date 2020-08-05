package com.sanxs.matcher;

import com.sanxs.matcher.function.GroupMatchFunction;
import com.sanxs.matcher.function.gorup.AbstractGroupByAggregateHandler;
import com.sanxs.utils.FunctionUtils;
import lombok.Getter;

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
                keysBuilder.append(FunctionUtils.getFieldNameAndValue(cache, data, this.objectKey));
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



}
