# SimpleDataHandler
Ant Financial Exam for &lt;Veys@qq.com>

## 项目介绍

通过构造 Where&lt;T&gt;、Group&lt;T,GroupTestData extends T&gt;、OrderBy&lt;T&gt;、Limit对List&lt;T&gt;进行复杂条件筛选以及聚合数据统计。

## 使用教程

### 一、条件查询(Where)

> 默认使用多线程模型，因是CPU密集型，线程数为核心数。

#### 1. 单个条件查询

```
public void handler(List<TestData> data){

    // 构造一个默认的数据处理器
    // 处理器处理的数据类型为TestData.class
    DataHandler<TestData> dataDataHandler = new SimpleDataHandler<>();

    // 构造一个简单的条件
    Where<TestData> where = new Where<>();
    
    // Where 过滤条件为 年龄大于 50；
    where.and((item) -> item.getAge() > 50);

    // 只需要传入条件字段即可完成筛选
    List<TestData> result = dataDataHandler.query(data, where, null, null);

}
```

#### 2. 多条件查询

```
public void handler(List<TestData> data){

    // 构造一个默认的数据处理器
    // 处理器处理的数据类型为TestData.class
    DataHandler<TestData> dataDataHandler = new SimpleDataHandler<>();

    // 构造条件对象
    Where<TestData> where = new Where<>();
    
    // Where 过滤条件为 age > 50 && id > 50 || gender = 0
    where
        .and((item) -> item.getAge() > 50)
        .and((item) -> item.getId() > 50)
        .or((item) -> item.getGender() = 0);

    // 只需要传入条件字段即可完成筛选
    List<TestData> result = dataDataHandler.query(data, where, null, null);

}
```

#### 3. 复杂条件查询

```
public void handler(List<TestData> data){

    // 构造一个默认的数据处理器
    // 处理器处理的数据类型为TestData.class
    DataHandler<TestData> dataDataHandler = new SimpleDataHandler<>();

    // 构造条件对象
    Where<TestData> where = new Where<>();
    
    // Where 过滤条件为 年龄是5或者3的整数倍或者性别为男
    where
        .and((item) -> item.getAge() % 5 == 0 || item.getAge() % 3 == 0); 
        .or((item) -> item.getGender() = 0);

    // 只需要传入条件字段即可完成筛选
    List<TestData> result = dataDataHandler.query(data, where, null, null);

}
```

### 二、分组查询(Group By)

#### 1. 基础用法

因为分组返回的对象类型会与入参类型不一致，所以在进行分组查询之前，需要定义一个对象接收分组返回值，该对象继承于入参对象。

可参考项目中`TestData`与`GroupTestData`,在`GroupTestData`中预先定义好需要聚合的字段。

```
public void handler(List<TestData> data){

    // 构建一个分组对象
    // 泛型参数第一个为入参类型，第二个为返回值如类型
    // 构造参数需要传入返回类型的class对象
    GroupBy<TestData,GroupTestData> gorupBy = new GroupBy<>(GroupTestData.class)

    // 设置分组的字段，使用lambda表达式
    // 代表gender字段作为分组的条件
    // 可以设置多个,最少一个。
    groupBy
         .appendKey(TestData::getGender)
         .appendKey(TestData::getAge);

    // 设置分组的聚合字段
    groupBy
        // Aggregates 内置了一些常用聚合方法，也可以继承AbstractGroupByAggregateHandler自定义聚合方法
        // Aggregates.max 第一个参数为聚合方法的数据来源，第二个参数是聚合结果的赋值字段
        // 例如求 TestData.id最大的数字，赋值到GroupTestData.maxId
        .appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId))
        .appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId));

    // 只需要传入Group即可完成分组
    List<TestData> result = dataDataHandler.query(data, null, groupBy, null);
    
    // 返回的结果为List<GroupTestData>,其中只有参与Group的字段与聚合字段才会被赋值，其他都是null或者初始值
}
```

#### 2. 高级用法(Having)

可以使用Having对聚合的结果进行聚合筛选，Having的实现原理与Where一致。

```
public void handler(List<TestData> data){

    GroupBy<TestData,GroupTestData> gorupBy = new GroupBy<>(GroupTestData.class)

    groupBy
            .appendKey(TestData::getGender)
            .appendKey(TestData::getAge);

    groupBy
        .appendAggregate(Aggregates.max(TestData::getId, GroupTestData::setMaxId))
        .appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId));

    // 获取having对象，进行拼装条件
    groupBy.getHaving()
            // 此处的条件拼装方式与Where一致（实现关系由Where进行实现）
            // 时间关系没有做强类型推断，所以只能保证正确的使用下会返回正确的结果（时间充足的话可以解决）
            // 正确使用：当使用having时，其条件字段必须为group by字段或者聚合结果字段
            .and(item -> item.getCountId() > 1);

    // 只需要传入Group即可完成分组
    List<TestData> result = dataDataHandler.query(data, null, groupBy, null);
}
```

### 三、排序查询（Order By）

#### 1. 基础用法

```
public void handler(List<TestData> data){
    OrderBy<TestData> orderBy = new OrderBy<>();
    // 定义需要排序的字段，最少一个。
    orderBy
        .appendDesc(TestData::getAge)
        .appendAsc(TestData::getId);

    // 传入排序对象即可
    List<TestData> result = dataDataHandler.query(data, orderBy, null, null);
}
```

#### 2. 高级用法（结合Group By）

```
public void handler(List<TestData> data){
    // 构造一个分组对象
    GroupBy<TestData, GroupTestData> groupBy = new GroupBy<>(GroupTestData.class);
    groupBy.appendKey(TestData::getAge);
    groupBy.appendAggregate(Aggregates.count(TestData::getId, GroupTestData::setCountId));

    // 构造一个排序对象
    OrderBy<TestData> orderBy = new OrderBy<>();
    // 当有group时候，排序的字段也只能是分组字段或者是分组聚合字段
    // 否则会告知非法排序字段
    orderBy.appendAsc(GroupTestData::getCountId);

    List<TestData> result = dataDataHandler.query(data, orderBy, groupBy, null );
}
```

### 三、分页查询（Limit）

#### 1. 基础用法

```
public void handler(List<TestData> data){
    // 构造一个分页对象
    // 参数： 起始位置，向后顺延多少条记录
    Limit limit = new Limit(0, 5);
    List<TestData> result = DATA_HANDLER.query(data, null, null, null, limit);

}
```

#### 2. 高级用法(group by)

同时支持对GroupBy的结果进行筛选，使用方式一致。

### 三、注意事项

该项目最小支持JDK 1.8
