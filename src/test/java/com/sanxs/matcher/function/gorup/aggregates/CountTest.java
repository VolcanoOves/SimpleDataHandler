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
public class CountTest {
    @Test
    public void test() {
        Count<TestData, GroupTestData> count = new Count<>(TestData::getId, GroupTestData::setCountId);

        List<TestData> data = new LinkedList<>();
        data.add(new TestData(1L, "张三", 26, 1));
        data.add(new TestData(2L, "李四", 28, 1));
        data.add(new TestData(3L, "赵五", 28, 0));
        data.add(new TestData(4L, "王六", 26, 0));

        data.forEach(count::handle);

        GroupTestData result = new GroupTestData();
        count.aggregate(result);

        Assert.assertEquals((long) result.getCountId(), data.size());
    }
}