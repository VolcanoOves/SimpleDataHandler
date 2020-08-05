package com.sanxs.matcher.service;

import com.sanxs.matcher.Limit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: 分页处理罗辑
 **/
public class LimitFilterServiceImpl<Data> implements LimitFilterService<Data> {
    @Override
    public List<Data> apply(List<Data> data, Limit limit) {
        if (limit != null) {
            // 如果group by 为空那么对于result进行分页
            List<Data> limitResult = new LinkedList<>();

            if (limit.getRows() < data.size()) {
                Iterator<Data> limitIterator = data.listIterator(limit.getRows());
                while (limitIterator.hasNext() && limitResult.size() < limit.getOffset()) {
                    Data temp = limitIterator.next();
                    limitResult.add(temp);
                }
            }
            return limitResult;
        }
        return data;
    }
}
