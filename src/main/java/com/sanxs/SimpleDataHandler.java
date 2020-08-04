package com.sanxs;

import com.sanxs.intf.DataHandler;
import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.Limit;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.Where;
import com.sanxs.matcher.service.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 简易版数据筛选器
 */
@Slf4j
public class SimpleDataHandler<Data> implements DataHandler<Data> {

    /**
     * 默认的where罗辑
     */
    private final WhereFilterService<Data> whereFilterService;

    /**
     * 分组处理罗辑
     */
    private final GroupByFilterService<Data> groupByFilterService;

    /**
     * 排序处理罗辑
     */
    private final OrderByFilterService<Data> orderByFilterService;

    /**
     * 分页处理罗辑
     */
    private final LimitFilterService<Data> limitFilterService;

    public SimpleDataHandler() {
        this.whereFilterService = new DefaultWhereFilterServiceImpl<>();
        this.groupByFilterService = new DefaultGroupByFilterServiceImpl<>();
        this.orderByFilterService = new DefaultOrderByFilterServiceImpl<>();
        this.limitFilterService = new LimitFilterServiceImpl<>();
    }

    @Override
    public List<Data> query(List<Data> data, Where<Data> where, OrderBy<Data> orderBy, GroupBy<Data, ?> groupBy, Limit limit) {

        long startTime = System.currentTimeMillis();

        // where 条件进行原始匹配，将匹配成功的值赋值于result 进行下一步计算
        List<Data> result = this.whereFilterService.apply(data, where);

        // 进行group匹配
        this.groupByFilterService.apply(result, groupBy);

        // 进行排序
        this.orderByFilterService.apply(result, orderBy, groupBy);

        // 进行分页
        result = this.limitFilterService.apply(result, limit);

        long endTime = System.currentTimeMillis();
        log.info("match successful time :{} ms", endTime - startTime);
        return result;
    }
}
