package com.sanxs;

import com.sanxs.intf.DataHandler;
import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.Limit;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.Where;
import com.sanxs.matcher.service.DefaultGroupByFilterServiceImpl;
import com.sanxs.matcher.service.DefaultWhereFilterServiceImpl;
import com.sanxs.matcher.service.GroupByFilterService;
import com.sanxs.matcher.service.WhereFilterService;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 简易版数据筛选器
 */
@Slf4j
public class SimpleDataHandler<Data> implements DataHandler<Data> {

    /**
     * 默认的where处理器
     */
    private final WhereFilterService<Data> whereFilterService;
    private final GroupByFilterService<Data> groupByFilterService;

    public SimpleDataHandler() {
        this.whereFilterService = new DefaultWhereFilterServiceImpl<>();
        this.groupByFilterService = new DefaultGroupByFilterServiceImpl<>();
    }

    @Override
    public List<Data> query(List<Data> data, Where<Data> where, OrderBy<Data> orderBy, GroupBy<Data, ?> groupBy, Limit limit) {

        long startTime = System.currentTimeMillis();

        // where 条件进行原始匹配，将匹配成功的值赋值于result 进行下一步计算
        List<Data> result = this.whereFilterService.apply(data, where);

        // 进行group匹配
        this.groupByFilterService.apply(result, groupBy);

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
            List<Data> limitResult = new LinkedList<>();

            Iterator<Data> limitIterator = result.listIterator(limit.getRows());
            while (limitIterator.hasNext() && limitResult.size() < limit.getOffset()) {
                Data temp = limitIterator.next();
                limitResult.add(temp);
            }
            result = limitResult;
        }

        long endTime = System.currentTimeMillis();
        log.info("match successful time :{} ms", endTime - startTime);
        return result;
    }
}
