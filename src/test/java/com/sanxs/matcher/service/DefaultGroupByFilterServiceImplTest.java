package com.sanxs.matcher.service;

import com.sanxs.data.GroupTestData;
import com.sanxs.data.TestData;
import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.function.gorup.Aggregates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yangshan
 * @Date: 2020/8/5
 * @Description:
 **/
class DefaultGroupByFilterServiceImplTest {


    /**
     * 单个分组
     */
    @Test
    public void singleGroupTest() {
        GroupByFilterService<TestData> groupByFilterService = new DefaultGroupByFilterServiceImpl<>();

        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);
        // 分组的字段
        groupBy.appendKey(TestData::getGender);
        // 分组的聚合字段
        groupBy
                .appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId))
                .appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId));

        List<TestData> data = new LinkedList<>();
        data.add(new TestData(1L, "张三", 26, 1));
        data.add(new TestData(2L, "李四", 28, 1));
        data.add(new TestData(3L, "赵五", 28, 0));
        data.add(new TestData(4L, "王六", 26, 0));

        List<TestData> result = groupByFilterService.apply(data, groupBy);

        List<TestData> answer = new LinkedList<>();
        // 聚合之后只显示分组字段与聚合字段
        answer.add(new GroupTestData(null, null, null, 1, 2L, null, 2L));
        answer.add(new GroupTestData(null, null, null, 0, 4L, null, 2L));

        Assertions.assertEquals(Arrays.toString(result.toArray()), Arrays.toString(answer.toArray()));
    }

}