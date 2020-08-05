package com.sanxs;

import com.sanxs.data.GroupTestData;
import com.sanxs.data.TestData;
import com.sanxs.intf.DataHandler;
import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.Limit;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.Where;
import com.sanxs.matcher.function.gorup.Aggregates;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: simple data handler 测试用例
 */
@Slf4j
public class DemoTest {

    public static final int DATA_COUNT = 50;
    private final static List<TestData> DATA_LIST;
    private final static DataHandler<TestData> DATA_HANDLER;


    static {
        DATA_LIST = new LinkedList<>();
        DATA_HANDLER = new SimpleDataHandler<>();

        Random random = new Random();
        for (int i = 0; i < DATA_COUNT; i++) {
            TestData testData = new TestData();
            testData.setId((long) i);
            testData.setAge(random.nextInt(100));
            testData.setName(UUID.randomUUID().toString());
            testData.setGender(random.nextInt(2));
            DATA_LIST.add(testData);
        }
    }

    /**
     * 单条件查询测试
     */
    @Test
    public void singleWhereTest() {
        Where<TestData> where = new Where<>();
        // 年龄小于10岁
        where.and(item -> item.getName().contains("ac"));

        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, where, null, null, null);
        this.printlnMatchData(result);
    }

    /**
     * 多条件查询测试
     */
    @Test
    public void multiWhereTest() {
        Where<TestData> where = new Where<>();
        // 年龄小于50岁
        where.and(item -> item.getAge() < 50)
                // 序号大于50岁
                .and(item -> item.getId() > 10)
                // 性别为女性
                .and(item -> item.getGender() == 1);

        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, where, null, null, null);
        this.printlnAllData(result);
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


        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, null, groupBy, null);
        this.printlnMatchData(result);
    }

    /**
     * 单分组查询测试
     */
    @Test
    public void multiGroupTest() {
        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);

        // 设置 group by 的字段
        groupBy
                .appendKey(TestData::getGender)
                .appendKey(TestData::getAge);

        groupBy
                .appendAggregate(Aggregates.avg(TestData::getAge, GroupTestData::setAvgAge))    // 求平均数
                .appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId))      // 最大ID
                .appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId)); // 统计计数

        groupBy.getHaving()
                .and(item -> item.getCountId() > 1);


        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, null, groupBy, null);
        this.printlnMatchData(result);
    }


    /**
     * 单个排序测试
     */
    @Test
    public void singleOrderByTest() {
        OrderBy<TestData> orderBy = new OrderBy<>();
        orderBy.appendDesc(TestData::getAge);

        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, orderBy, null, null);
        this.printlnMatchData(result);
    }

    /**
     * 多个排序测试
     */
    @Test
    public void multiOrderByTest() {
        OrderBy<TestData> orderBy = new OrderBy<>();
        orderBy.appendDesc(TestData::getAge);
        orderBy.appendAsc(TestData::getId);

        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, orderBy, null, null);
        this.printlnMatchData(result);
    }

    /**
     * 分页处理器
     */
    @Test
    public void limitTest() {
        Limit limit = new Limit(49, 5);
        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, null, null, limit);
        this.printlnMatchData(result);
    }

    /**
     * Group and order 进行排序
     * 同时以group聚合之后的字段进行排序
     */
    @Test
    public void groupAndOrder() {
        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);
        groupBy.appendKey(TestData::getAge);
        groupBy.appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId));

        OrderBy<TestData> orderBy = new OrderBy<>();
        orderBy.appendAsc(GroupTestData::getCountId);


        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, orderBy, groupBy, null);
        this.printlnMatchData(result);
    }

    /**
     * 分组加分页
     */
    @Test
    public void groupAndLimit() {
        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);

        // 设置 group by 的字段
        groupBy
                .appendKey(TestData::getGender)
                .appendKey(TestData::getAge);

        groupBy
                .appendAggregate(Aggregates.avg(TestData::getAge, GroupTestData::setAvgAge))    // 求平均数
                .appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId))      // 最大ID
                .appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId)); // 统计计数

        Limit limit = new Limit(0, 10);

        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, null, groupBy, limit);
        this.printlnMatchData(result);
    }

    /**
     * 条件加分组
     */
    @Test
    public void whereAndGroup() {
        GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);

        // 设置 group by 的字段
        groupBy
                .appendKey(TestData::getGender)
                .appendKey(TestData::getAge);

        groupBy
                .appendAggregate(Aggregates.avg(TestData::getAge, GroupTestData::setAvgAge))    // 求平均数
                .appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId))      // 最大ID
                .appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId)); // 统计计数

        Where<TestData> where = new Where<>();

        where
                .and((item) -> item.getAge() > 50)
                .and((item) -> item.getGender() == 1);

        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, where, null, groupBy, null);
        this.printlnMatchData(result);
    }


    /**
     * 输出匹配前的数据
     *
     * @param testDatas 匹配前数据
     */
    private void printlnAllData(List<TestData> testDatas) {
        log.info(String.format("------------------------- 匹配之前的数据共%6d条 -------------------------", testDatas.size()));
        for (TestData testData : testDatas) {
            log.info(testData.toString());
        }
    }

    /**
     * 输出匹配后的数据
     *
     * @param matchDatas 匹配后数据
     */
    private <T> void printlnMatchData(List<TestData> matchDatas) {
        log.info(String.format("------------------------- 匹配之后的数据共%6d条 -------------------------", matchDatas.size()));
        for (TestData testData : matchDatas) {
            log.info(testData.toString());
        }
    }
}
