package com.sanxs.matcher.service;

import com.sanxs.data.TestData;
import com.sanxs.matcher.OrderBy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/4
 * @Description:
 */
class DefaultOrderByFilterServiceImplDemoTest {
    @Test
    public void singleOrder() {
        List<TestData> data = new LinkedList<>();
        data.add(new TestData(1L, "张三", 26, 1));
        data.add(new TestData(2L, "李四", 28, 1));
        data.add(new TestData(3L, "赵五", 28, 0));
        data.add(new TestData(4L, "王六", 26, 0));

        OrderBy<TestData> orderBy = new OrderBy<>();
        orderBy.appendDesc(TestData::getId);

        OrderByFilterService<TestData> orderByFilterService = new DefaultOrderByFilterServiceImpl<>();
        orderByFilterService.apply(data, orderBy, null);

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < i; j++) {
                Assertions.assertTrue(data.get(i).getId() <= data.get(j).getId());
            }
        }
    }

    @Test
    public void multiOrder() {
        List<TestData> data = new LinkedList<>();
        data.add(new TestData(1L, "张三", 26, 1));
        data.add(new TestData(2L, "李四", 28, 1));
        data.add(new TestData(3L, "赵五", 28, 0));
        data.add(new TestData(4L, "王六", 26, 0));

        OrderBy<TestData> orderBy = new OrderBy<>();
        orderBy
                .appendAsc(TestData::getAge)
                .appendDesc(TestData::getId);

        OrderByFilterService<TestData> orderByFilterService = new DefaultOrderByFilterServiceImpl<>();
        orderByFilterService.apply(data, orderBy, null);

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < i; j++) {
                Assertions.assertTrue(data.get(i).getAge() >= data.get(j).getAge());
                if (data.get(i).getAge().equals(data.get(j).getAge())) {
                    Assertions.assertTrue(data.get(i).getId() <= data.get(j).getId());
                }
            }
        }
    }
}