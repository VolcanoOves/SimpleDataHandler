package com.sanxs.matcher.service;

import com.sanxs.data.TestData;
import com.sanxs.matcher.Where;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/4
 * @Description: 条件筛选测试
 */
class DefaultWhereFilterServiceImplDemoTest {

    @Test
    public void where() {
        List<TestData> data = new LinkedList<>();
        data.add(new TestData(1L, "张三", 26, 1));
        data.add(new TestData(2L, "李四", 28, 1));
        data.add(new TestData(3L, "赵五", 28, 0));
        data.add(new TestData(4L, "王六", 26, 0));

        Where<TestData> where = new Where<>();
        where
                .and(item -> item.getGender() == 0)
                .and(item -> item.getAge() == 26);

        WhereFilterService<TestData> whereFilterService = new DefaultWhereFilterServiceImpl<>();
        List<TestData> result = whereFilterService.apply(data, where);

        result.forEach(item -> {
            Assertions.assertEquals(0, (int) item.getGender());
            Assertions.assertEquals(26, (int) item.getAge());
        });
    }

}