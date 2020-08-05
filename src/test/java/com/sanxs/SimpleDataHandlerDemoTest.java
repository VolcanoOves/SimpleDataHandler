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

    private final static DataHandler<TestData> DATA_HANDLER;


    static {
        DATA_HANDLER = new SimpleDataHandler<>();
    }

    public List<TestData> getData() {
        List<TestData> testData = new LinkedList<>();
        testData.add(new TestData(1L, "张三", 26, 1));
        testData.add(new TestData(2L, "李四", 28, 1));
        testData.add(new TestData(3L, "赵五", 28, 0));
        testData.add(new TestData(4L, "王六", 26, 0));
        return testData;
    }

    /**
     * 单条件查询测试
     */
    @Test
    public void singleWhereTest() {
        Where<TestData> where = new Where<>();

        // ID > 1
        where.and(item -> item.getId() > 1);

        List<TestData> result = DATA_HANDLER.query(getData(), where, null, null, null);

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
                .and(item -> item.getGender() == 1)     // gender == 1
                .or(item -> item.getId() == 4);         // id == 4

        List<TestData> result = DATA_HANDLER.query(getData(), where, null, null, null);

        List<TestData> answer = new LinkedList<>();
        answer.add(new TestData(2L, "李四", 28, 1));
        answer.add(new TestData(4L, "王六", 26, 0));

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

        List<TestData> result = DATA_HANDLER.query(getData(), null, null, groupBy, null);

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

        List<TestData> result = DATA_HANDLER.query(getData(), null, orderBy, null, null);

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

        List<TestData> result = DATA_HANDLER.query(getData(), null, null, null, limit);

        List<TestData> answer = new LinkedList<>();
        answer.add(new TestData(4L, "王六", 26, 0));

        Assert.assertEquals(Arrays.toString(answer.toArray()), Arrays.toString(result.toArray()));
    }

    /**
     * 在group情况下使用非group字段方式进行排序
     */
    @Test
    public void errorGroupAndOrder() {
        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);
        groupBy.appendKey(TestData::getId);
        groupBy.appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId));

        OrderBy<TestData> orderBy = new OrderBy<>();
        orderBy.appendDesc(TestData::getAge);

        Exception exception = null;
        try {
            DATA_HANDLER.query(getData(), null, orderBy, groupBy, null);
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertNotNull(exception);

    }

    /**
     * 空字段排序
     */
    @Test
    public void nullFieldOrder() {
        List<TestData> testData = getData();

        testData.get(1).setId(null);
        testData.get(3).setId(null);

        OrderBy<TestData> orderBy = new OrderBy<>();
        orderBy.appendDesc(TestData::getId);

        DATA_HANDLER.query(testData, null, orderBy, null, null);
    }

    /**
     * 0 分组报错
     */
    @Test
    public void zeroGroup() {
        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);

        Exception exception = null;
        try {
            DATA_HANDLER.query(getData(), null, null, groupBy, null);
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }
}