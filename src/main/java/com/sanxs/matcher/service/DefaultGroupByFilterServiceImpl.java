package com.sanxs.matcher.service;

import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.function.GroupMatchFunction;
import com.sanxs.matcher.function.gorup.AbstractGroupByAggregateHandler;
import com.sanxs.matcher.function.gorup.AggregateFunction;
import com.sanxs.matcher.function.gorup.GetFieldFunction;
import com.sanxs.utils.CopyUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description:
 **/
public class DefaultGroupByFilterServiceImpl<Data> implements GroupByFilterService<Data> {

    @Override
    @SuppressWarnings("unchecked")
    public <GroupData extends Data> List<Data> apply(List<Data> data, GroupBy<Data, GroupData> groupBy) {

        // 进行group匹配
        if (groupBy != null) {
            // 存放分组的数据
            Map<GroupBy.GroupKey<Data>, GroupData> groupMap = new ConcurrentHashMap<>(16);
            // 存放每个分组的统计快照，统计罗辑是group.getAggregateHandlers()的副本
            Map<String, AbstractGroupByAggregateHandler<Data, GroupData, ?, ?>> handlerMap = new ConcurrentHashMap<>(16);

            // 获取分组匹配的规则
            Collection<GroupMatchFunction<Data, ?>> groupMatchFunctions = groupBy.getGroupMatchFunctions();
            data.forEach(item -> {
                // 获取分组的key
                GroupBy.GroupKey<Data> groupKey = new GroupBy.GroupKey<>(groupMatchFunctions, item);

                if (!groupMap.containsKey(groupKey)) {
                    try {
                        // 拼装一个只有group条件的对象
                        GroupData objectKey = groupBy.getGroupDataClass().newInstance();
                        CopyUtils.copy(groupKey.getObjectKey(), objectKey);
                        groupMap.putIfAbsent(groupKey, objectKey);

                        groupBy.getAggregateHandlers().forEach(handler -> {
                            // 将处理罗辑进行副本创建
                            // 时间关系没有能够设计出分组隔离的模型...
                            AbstractGroupByAggregateHandler<Data, GroupData, Object, Object> clone = null;
                            try {
                                clone = handler.getClass().newInstance();
                                CopyUtils.copy(handler, clone);
                                clone.setIn((GetFieldFunction<Data, Object>) handler.getIn());
                                clone.setOut((AggregateFunction<GroupData, Object>) handler.getOut());
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("clone fail : " + handler.getClass().getName());
                            }
                            handlerMap.put(groupKey.getKey() + "." + clone.getClass().getName(), clone);
                        });

                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                // 在遍历的过程中进行聚合统计
                for (AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> handler : groupBy.getAggregateHandlers()) {
                    // 通过源对象的class作为索引查找出clone对象
                    AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> clone = handlerMap.get(groupKey.getKey() + "." + handler.getClass().getName());
                    // 这里在分组隔离的副本当中聚合统计
                    clone.handle(item);
                }

            });

            // 当这里所有group已经处理完成之后
            // 进行聚合结果的赋值
            groupMap.forEach((key, value) -> {
                for (AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> handler : groupBy.getAggregateHandlers()) {
                    AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> clone = handlerMap.get(key.getKey() + "." + handler.getClass().getName());
                    clone.aggregate(value);
                }
            });

            data.clear();
            data.addAll(groupMap.values());
        }
        return data;
    }
}
