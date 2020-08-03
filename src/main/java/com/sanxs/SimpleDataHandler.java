package com.sanxs;

import com.sanxs.enums.WhereMatchDelimiterEnum;
import com.sanxs.intf.DataHandler;
import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.Limit;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.Where;
import com.sanxs.matcher.function.GroupMatchFunction;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 简易版数据筛选器
 */
@Slf4j
public class SimpleDataHandler implements DataHandler {

    @Override
    public <T> List<T> query(List<T> data, Where<T> where, OrderBy<T> orderBy, GroupBy<T> groupBy, Limit limit) {

        long startTime = System.currentTimeMillis();

        List<T> result;

        // where 条件进行原始匹配，将匹配成功的值赋值于result 进行下一步计算
        if (where != null) {
            result = data.stream().filter(item -> {
                // 将where中的所有条件节点拿出来
                Iterator<Where<T>.WhereMatchFunctionWrapper> iterator = where.getWhereMatchFunctions().iterator();
                // 默认是数据满足条件
                boolean matchSuccess = true;
                // 遍历进行条件匹配，类似于递归进行调用
                while (iterator.hasNext()) {
                    // 拿到当前条件
                    Where<T>.WhereMatchFunctionWrapper wrapper = iterator.next();
                    // 如果当前条件是AND拼接，那么与当前计算的值进行‘&&’运算
                    if (wrapper.getDelimiter().equals(WhereMatchDelimiterEnum.AND)) {
                        matchSuccess = matchSuccess && wrapper.getFunction().match(item);
                    } else {
                        // 如果当前条件是OR拼接，那么与当前计算的值进行‘||’运算
                        matchSuccess = matchSuccess || wrapper.getFunction().match(item);
                    }
                }
                // 返回是否匹配
                return matchSuccess;
            }).collect(Collectors.toList());
        } else {
            result = data;
        }

        // 进行group 匹配
        if (groupBy != null) {
            // 存放分组的数据
            Map<GroupBy.GroupKey<T>, List<T>> groupMap = new ConcurrentHashMap<>();
            // 获取分组匹配的规则
            Collection<GroupMatchFunction<T, ?>> groupMatchFunctions = groupBy.getGroupMatchFunctions();
            result.forEach(item -> {
                GroupBy.GroupKey<T> groupKey = new GroupBy.GroupKey<>(groupMatchFunctions, item);
                if (!groupMap.containsKey(groupKey)) {
                    groupMap.putIfAbsent(groupKey, new LinkedList<>());
                }
                groupMap.get(groupKey).add(item);
            });
            groupBy.getData().putAll(groupMap);
        }

        // 进行排序
        if (orderBy != null) {
            // 如果group by 为空那么对于result进行排序
            if (groupBy == null) {
                // todo result 排序
            } else {
                // 否则对group by data 中的 key进行排序
                // todo group by keys 排序
            }
        }

        // 进行分页
        if (limit != null) {
            // 如果group by 为空那么对于result进行分页
            if (groupBy == null) {
                // todo 分页
            } else {
                // 否则对group by data 中的 key进行分页
                // TODO 排序
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("match successful time :{} ms", endTime - startTime);
        return result;
    }
}
