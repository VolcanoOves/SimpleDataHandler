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
 * @Description: 分组模型对象
 */
@Getter
public class GroupBy<Data, GroupData extends Data> {

    /**
     * 需要进行group的字段列表
     */
    private final List<GroupMatchFunction<Data, ?>> groupMatchFunctions;

    /**
     * 需要集合字段以及聚合结果输出的位置
     */
    private final List<AbstractGroupByAggregateHandler<Data, GroupData, ?, ?>> aggregateHandlers;

    /**
     * 聚合结果返回的类型
     */
    private final Class<GroupData> groupDataClass;

    /**
     * having 简单版实现（基于where）
     */
    private final Where<GroupData> having;

    public GroupBy(Class<GroupData> clazz) {
        this.groupMatchFunctions = new LinkedList<>();
        this.aggregateHandlers = new LinkedList<>();
        this.groupDataClass = clazz;
        this.having = new Where<>();
    }

    /**
     * 增加需要group by的字段
     *
     * @param groupByKey lambda表达式 TestData::getId 表示用ID进行分组
     * @return this
     */
    public GroupBy<Data, GroupData> appendKey(GroupMatchFunction<Data, ?> groupByKey) {
        this.groupMatchFunctions.add(groupByKey);
        return this;
    }

    /**
     * 增加聚合罗辑，以及需要聚合的字段
     *
     * @param groupByKey 聚合罗辑
     *                   new Avg<TestData::getAge, GroupTestData::setAvgAge>
     *                   代表用TestData age字段进行平均数聚合 聚合结果填入 GroupTestData avgAge
     * @return this
     */
    public GroupBy<Data, GroupData> appendAggregate(AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> groupByKey) {
        this.aggregateHandlers.add(groupByKey);
        return this;
    }


    /**
     * group key
     * 用于group字段组成唯一索引
     *
     * @param <Data>
     */
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
