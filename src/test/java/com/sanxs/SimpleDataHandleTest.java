package com.sanxs;

import com.sanxs.data.TestData;
import com.sanxs.intf.DataHandler;
import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.Where;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: simple data handler 测试用例
 */
@Slf4j
public class SimpleDataHandleTest {

    public static final int DATA_COUNT = 500;
    private final static List<TestData> DATA_LIST;
    private final static DataHandler DATA_HANDLER;


    static {
        DATA_LIST = new LinkedList<>();
        DATA_HANDLER = new SimpleDataHandler();

        Random random = new Random();
        for (int i = 0; i < DATA_COUNT; i++) {
            DATA_LIST.add(
                    TestData.builder()
                            .id((long) i)
                            .age(random.nextInt(100))
                            .name(UUID.randomUUID().toString())
                            .gender(random.nextInt(2))
                            .build()
            );
        }
    }

    /**
     * 单条件查询测试
     */
    @Test
    public void singleWhereTest() {
        Where<TestData> where = new Where<>();
        // 年龄小于10岁
        where.and(item -> item.getAge() < 10);

        this.printlnAllData(DATA_LIST);
        List<TestData> result = DATA_HANDLER.query(DATA_LIST, where, null, null, null);
        this.printlnAllData(result);
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
        GroupBy<TestData> groupBy = new GroupBy<>();
        groupBy.append(TestData::getGender);

        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, null, groupBy, null);
        this.printlnMatchData(result, groupBy);
    }

    /**
     * 单分组查询测试
     */
    @Test
    public void multiGroupTest() {
        GroupBy<TestData> groupBy = new GroupBy<>();
        groupBy
                .append(TestData::getGender)
                .append(TestData::getAge);

        List<TestData> result = DATA_HANDLER.query(DATA_LIST, null, null, groupBy, null);
        this.printlnMatchData(result, groupBy);
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
     * @param groupBy    因为分组之后的输出结果不是原来的结构，所以需要传入是否分组，根据参数展示不同的数据
     */
    private <T> void printlnMatchData(List<TestData> matchDatas, GroupBy<T> groupBy) {
        if (groupBy == null) {
            log.info(String.format("------------------------- 匹配之后的数据共%6d条 -------------------------", matchDatas.size()));
            for (TestData testData : matchDatas) {
                log.info(testData.toString());
            }
        } else {
            log.info(String.format("------------------------- Group匹配之后的数据共%6d条 -------------------------", matchDatas.size()));
            for (Map.Entry<GroupBy.GroupKey<T>, List<T>> entry : groupBy.getData().entrySet()) {
                log.info("group by -> {}，size : {} 条", entry.getKey().getKey(), entry.getValue().size());
                for (T testData : entry.getValue()) {
                    log.info(testData.toString());
                }
            }
        }
    }
}
