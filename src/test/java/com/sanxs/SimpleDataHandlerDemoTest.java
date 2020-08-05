package com.sanxs;

import com.sanxs.data.GroupTestData;
import com.sanxs.data.TestData;
import com.sanxs.intf.DataHandler;
import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.Limit;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.Where;
import com.sanxs.matcher.function.gorup.Aggregates;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/4
 * @Description: 测试用例
 */
public class SimpleDataHandlerDemoTest {

    private final static List<TestData> DATA_LIST;
    private final static DataHandler<TestData> DATA_HANDLER;


    static {
        DATA_LIST = new LinkedList<>();
        DATA_HANDLER = new SimpleDataHandler<>();

        DATA_LIST.add(new TestData(1L, "张三", 26, 1));
        DATA_LIST.add(new TestData(2L, "李四", 28, 1));
        DATA_LIST.add(new TestData(3L, "赵五", 28, 0));
        DATA_LIST.add(new TestData(4L, "王六", 26, 0));
    }

    /**
     * 单条件查询测试
     */
    @Test
    public void singleWhereTest() {
        Where<TestData> where = new Where<>();

        // ID > 1
        where.and(item -> item.getId() > 1);

        List<TestData> result = DATA_HANDLER.query(DATA_LIST, where, null, null, null);

        List<TestData> answer = new LinkedList<>();
        answer.add(new TestData(2L, "李四", 28, 1));
        answer.add(new TestData(3L, "赵五", 28, 0));
        answer.add(new TestData(4L, "王六", 26, 0));

        Assert.assertEquals(Arrays.toString(answer.toArray()), Arrays.toString(result.toArray()));
    }

    /**
     * 多条件查询测试
     */
    @Test
    public void multiWhereTest() {
        Where<TestData> where = new Where<>();


        where
                .and(item -> item.getId() > 1)          // ID > 1
                .and(item -> item.getGender() == 1);    // gender == 1

        List<TestData> result = DATA_HANDLER.query(DATA_LIST, where, null, null, null);

        List<TestData> answer = new LinkedList<>();
        answer.add(new TestData(2L, "李四", 28, 1));

        Assert.assertEquals(Arrays.toString(answer.toArray()), Arrays.toString(result.toArray()));

    }

    /**
     * 单分组查询测试
     */
    @Test
    public void singleGroupTest() {
        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);

        // 设置 group by 的字段
        groupBy.appendKey(TestData::getGender);

        groupBy
                .appendAggregate(Aggregates.avg(TestData::getAge, GroupTestData::setAvgAge))    // 求平均数
                .appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId))      // 最大ID
                .appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId)); // 统计计数

        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, null, groupBy, null);

        List<GroupTestData> answer = new LinkedList<>();
        answer.add(new GroupTestData(null, null, null, 1, 2L, 27d, 2L));
        answer.add(new GroupTestData(null, null, null, 0, 4L, 27d, 2L));

        Assert.assertEquals(Arrays.toString(answer.toArray()), Arrays.toString(result.toArray()));

    }

    /**
     * 单字段排序测试
     */
    @Test
    public void singleOrderByTest() {
        OrderBy<TestData> orderBy = new OrderBy<>();
        orderBy.appendDesc(TestData::getId);

        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, orderBy, null, null);

        List<TestData> answer = new LinkedList<>();

        answer.add(new TestData(4L, "王六", 26, 0));
        answer.add(new TestData(3L, "赵五", 28, 0));
        answer.add(new TestData(2L, "李四", 28, 1));
        answer.add(new TestData(1L, "张三", 26, 1));

        Assert.assertEquals(Arrays.toString(answer.toArray()), Arrays.toString(result.toArray()));
    }

    /**
     * 分页测试
     */
    @Test
    public void limitTest() {
        Limit limit = new Limit(3, 1);

        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, null, null, limit);

        List<TestData> answer = new LinkedList<>();
        answer.add(new TestData(4L, "王六", 26, 0));

        Assert.assertEquals(Arrays.toString(answer.toArray()), Arrays.toString(result.toArray()));
    }

}