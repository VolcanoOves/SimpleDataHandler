package com.sanxs.matcher.service;

import com.sanxs.data.TestData;
import com.sanxs.matcher.Limit;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/4
 * @Description:
 */
public class LimitFilterServiceImplDemoTest {

    /**
     * 常见分页
     */
    @Test
    public void limit() {

        List<TestData> data = new LinkedList<>();
        data.add(new TestData(1L, "张三", 26, 1));
        data.add(new TestData(2L, "李四", 28, 1));
        data.add(new TestData(3L, "赵五", 28, 0));
        data.add(new TestData(4L, "王六", 26, 0));

        Limit limit = new Limit(0, 1);

        LimitFilterServiceImpl<TestData> limitFilterService = new LimitFilterServiceImpl<>();
        List<TestData> result = limitFilterService.apply(data, limit);

        List<TestData> answer = new LinkedList<>();
        answer.add(new TestData(1L, "张三", 26, 1));

        Assert.assertEquals(Arrays.toString(result.toArray()), Arrays.toString(answer.toArray()));
    }

    /**
     * 大于分页范围
     */
    @Test
    public void limitOutBoundIndex() {

        List<TestData> data = new LinkedList<>();
        data.add(new TestData(1L, "张三", 26, 1));
        data.add(new TestData(2L, "李四", 28, 1));
        data.add(new TestData(3L, "赵五", 28, 0));
        data.add(new TestData(4L, "王六", 26, 0));

        Limit limit = new Limit(10, 1);

        LimitFilterServiceImpl<TestData> limitFilterService = new LimitFilterServiceImpl<>();
        List<TestData> result = limitFilterService.apply(data, limit);

        Assert.assertTrue(result.isEmpty());
    }
}