package com.sanxs.matcher.function.gorup.aggregates;

import com.sanxs.data.GroupTestData;
import com.sanxs.data.TestData;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yangshan
 * @Date: 2020/8/5
 * @Description:
 **/
public class MaxTest {
    @Test
    public void test() {
        Max<TestData, GroupTestData> count = new Max<>(TestData::getId, GroupTestData::setMaxId);

        List<TestData> data = new LinkedList<>();
        data.add(new TestData(1L, "张三", 26, 1));
        data.add(new TestData(2L, "李四", 28, 1));
        data.add(new TestData(3L, "赵五", 28, 0));
        data.add(new TestData(4L, "王六", 26, 0));

        data.forEach(count::handle);

        GroupTestData result = new GroupTestData();
        count.aggregate(result);

        Assert.assertEquals(4, (long) result.getMaxId());
    }
}