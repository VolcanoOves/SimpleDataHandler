package com.sanxs.matcher.service;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.sanxs.enums.WhereMatchDelimiterEnum;
import com.sanxs.matcher.Where;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: 条件筛选处理罗辑
 **/
@Slf4j
public class DefaultWhereFilterServiceImpl<Data> implements WhereFilterService<Data> {

    private final int processors = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            1,
            processors,
            1000L,
            TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(9999),
            new ThreadFactoryBuilder().setNamePrefix("where-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

    @Override
    public synchronized List<Data> apply(List<Data> data, Where<Data> where) {

        if (where != null) {
            List<Data> result = new LinkedList<>();
            List<Future<List<Data>>> futures = new ArrayList<>(processors);

            // 当前数据处理游标
            int cursor = 0;
            // 最小步长为 0
            int step = (data.size() / processors) > 0 ? data.size() / processors : 1;

            for (int i = 0; i < processors && cursor < data.size(); i++) {

                final int start = cursor;
                final int end = i != processors - 1 ? cursor + step : data.size();
                Future<List<Data>> future = executorService.submit(() -> {
                    List<Data> matchSuccesses = new LinkedList<>();

                    // 使用迭代器尽可能的优化性能
                    // 取到游标地址的开始节点
                    Iterator<Data> iterator = data.listIterator(start);
                    // 向下遍历end-start个节点
                    for (int j = 0; j < end - start; j++) {
                        Data item = iterator.next();
                        // 满足条件则添加到匹配成功的列表
                        if (this.validation(item, where)) {
                            matchSuccesses.add(item);
                        }
                    }

                    return matchSuccesses;
                });

                // 更新游标
                cursor = end;
                futures.add(future);
            }
            for (Future<List<Data>> future : futures) {
                try {
                    result.addAll(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            return result;
        } else {
            return data;
        }
    }

    @Override
    public boolean validation(Data data, Where<Data> where) {
        // 将where中的所有条件节点拿出来
        Iterator<Where<Data>.WhereMatchFunctionWrapper> iterator = where.getWhereMatchFunctions().iterator();
        // 默认是数据满足条件
        boolean matchSuccess = true;
        // 遍历进行条件匹配，类似于递归进行调用
        while (iterator.hasNext()) {
            // 拿到当前条件
            Where<Data>.WhereMatchFunctionWrapper wrapper = iterator.next();
            // 如果当前条件是AND拼接，那么与当前计算的值进行‘&&’运算
            if (wrapper.getDelimiter().equals(WhereMatchDelimiterEnum.AND)) {
                matchSuccess = matchSuccess && wrapper.getFunction().match(data);
            } else {
                // 如果当前条件是OR拼接，那么与当前计算的值进行‘||’运算
                matchSuccess = matchSuccess || wrapper.getFunction().match(data);
            }
        }
        // 返回是否匹配
        return matchSuccess;
    }
}
