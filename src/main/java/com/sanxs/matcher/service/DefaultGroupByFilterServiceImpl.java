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
import java.util.stream.Collectors;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: 分组处理罗辑
 **/
public class DefaultGroupByFilterServiceImpl<Data> implements GroupByFilterService<Data> {

    @Override
    @SuppressWarnings("unchecked")
    public <GroupData extends Data> List<Data> apply(List<Data> data, GroupBy<Data, GroupData> groupBy) {

        // 进行group匹配
        if (groupBy != null) {

            if (groupBy.getGroupMatchFunctions().isEmpty()) {
                throw new RuntimeException("分组字段最少1个");
            }

            WhereFilterService<GroupData> whereFilterService = new DefaultWhereFilterServiceImpl<>();

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
                            // 将处理罗辑进行副本创建 因为处理结果的缓存数据存放在罗辑对象中（没有设计好）
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

            // 通过having将条件筛选出来
            // 由于时间关系没有做好字段类型的检测，没能判断哪些字段能够在having中使用
            // 其原因是在设计where的时候，使用灵活的自己设置判断条件，保持了灵活失去了类型检测（时间来不及改了 - - ）
            // 如果传入非法字段进行having匹配直接会返回异常的匹配集合
            data = data.stream().filter(item -> whereFilterService.validation((GroupData) item, groupBy.getHaving())).collect(Collectors.toList());
        }
        return data;
    }
}
