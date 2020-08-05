package com.sanxs;

import com.sanxs.data.GroupTestData;
import com.sanxs.data.TestData;
import com.sanxs.intf.DataHandler;
import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.Limit;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.Where;
import com.sanxs.matcher.function.gorup.Aggregates;
import com.sanxs.matcher.function.gorup.aggregates.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

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

    @Test
    public void createAggregates() {
        Avg<TestData, GroupTestData> avg = new Avg<>();
        Count<TestData, GroupTestData> count = new Count<>();
        Max<TestData, GroupTestData> max = new Max<>();
        Min<TestData, GroupTestData> min = new Min<>();
        Sum<TestData, GroupTestData> sum = new Sum<>();
        Assert.assertNotNull(avg);
        Assert.assertNotNull(count);
        Assert.assertNotNull(max);
        Assert.assertNotNull(min);
        Assert.assertNotNull(sum);
    }

    /**
     * 复杂查询
     */
    @Test
    public void complexQuery() {
        // 构造20个对象
        List<TestData> all = new LinkedList<>();
        for (int i = 0; i < 200; i++) {
            if (i % 50 == 0) {
                all.add(new TestData(null, null, null, null));
            } else {
                all.add(new TestData((long) i, UUID.randomUUID().toString(), new Random().nextInt(100), new Random().nextInt(2)));
            }
        }


        Where<TestData> where = new Where<>();
        where
                .and(item -> item.getId() % 2 == 0)     // id 是 偶数
                .or(item -> item.getGender() == 0);     // 性别女

        List<TestData> result1 = DATA_HANDLER.query(all, where, null, null, null);

        result1.forEach(item -> {
            Assert.assertTrue(item.getId() % 2 == 0 || item.getGender() == 0);
        });

        System.out.println("step 1 ====================================================");
        result1.forEach(System.out::println);

        // ===============================================================================

        OrderBy<TestData> orderBy = new OrderBy<>();
        // 按照ID正序 年龄反序
        orderBy
                .appendAsc(TestData::getGender)
                .appendDesc(TestData::getAge);

        List<TestData> result2 = DATA_HANDLER.query(all, where, orderBy, null, null);

        result2.forEach(item -> {
            Assert.assertTrue(item.getId() % 2 == 0 || item.getGender() == 0);
        });

        for (int i = 0; i < result2.size(); i++) {
            for (int j = 0; j < i; j++) {
                Assert.assertTrue(result2.get(i).getGender() >= result2.get(j).getGender());
                if (result2.get(i).getGender().equals(result2.get(j).getGender())) {
                    Assert.assertTrue(result2.get(i).getAge() <= result2.get(j).getAge());
                }
            }
        }

        System.out.println("step 2 ====================================================");
        result2.forEach(System.out::println);

        // ===============================================================================

        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);
        // 按照性别 年龄分组
        groupBy
                .appendKey(TestData::getGender)
                .appendKey(TestData::getAge);

        // 清空条件
        orderBy.getOrderMatchFunctions().clear();
        // 年龄排序 聚合count排序
        orderBy
                .appendAsc(TestData::getAge)
                .appendDesc(GroupTestData::getCountId);

        // 聚合统计 最大ID
        // 总计个数
        // 平均年龄
        groupBy
                .appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId))
                .appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId))
                .appendAggregate(Aggregates.avg(TestData::getAge, GroupTestData::setAvgAge));

        groupBy.getHaving()
                .and(item -> item.getAvgAge() > 50);    // 平均年龄大于50的分组

        List<TestData> result3 = DATA_HANDLER.query(all, where, orderBy, groupBy, null);

        Set<String> groupKeys = new HashSet<>();
        all.forEach(item -> groupKeys.add(item.getGender() + "." + item.getAge()));

        for (int i = 0; i < result3.size(); i++) {
            for (int j = 0; j < i; j++) {
                GroupTestData groupTestData1 = (GroupTestData) result3.get(i);
                GroupTestData groupTestData2 = (GroupTestData) result3.get(j);
                Assert.assertTrue(groupTestData1.getAvgAge() >= groupTestData2.getAvgAge());
                if (groupTestData1.getAvgAge().equals(groupTestData2.getAvgAge())) {
                    Assert.assertTrue(groupTestData1.getCountId() <= groupTestData2.getCountId());
                }
            }
        }

        Set<String> trueGroupKey = result3.stream().map(item -> item.getGender() + "." + item.getAge()).collect(Collectors.toSet());
        Assert.assertTrue(groupKeys.containsAll(trueGroupKey));

        result3.forEach(item -> {
            GroupTestData groupTestData = (GroupTestData) item;
            Assert.assertTrue(groupTestData.getAvgAge() > 50);
        });

        System.out.println("step 3 ====================================================");
        result3.forEach(System.out::println);

        // =============================================================================

        Limit limit = new Limit(0, 1);
        List<TestData> result4 = DATA_HANDLER.query(all, where, orderBy, groupBy, limit);
        Assert.assertEquals(1, result4.size());

        System.out.println("step 4 ====================================================");
        result4.forEach(System.out::println);
    }
}