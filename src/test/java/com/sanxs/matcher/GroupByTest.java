package com.sanxs.matcher;

import com.sanxs.data.TestData;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

/**
 * @Author: Yangshan
 * @Date: 2020/8/5
 * @Description:
 **/
public class GroupByTest {

    /**
     * GroupKey key 一样则一样
     */
    @Test
    public void groupKeyEquals() {
        GroupBy.GroupKey<TestData> groupKey1 = new GroupBy.GroupKey<>(new LinkedList<>(), new TestData());
        GroupBy.GroupKey<TestData> groupKey2 = new GroupBy.GroupKey<>(new LinkedList<>(), new TestData());
        Assert.assertTrue(groupKey1.equals(groupKey1));
        Assert.assertTrue(groupKey1.equals(null) == false);
        Assert.assertTrue(groupKey1.equals(groupKey2));
    }

}