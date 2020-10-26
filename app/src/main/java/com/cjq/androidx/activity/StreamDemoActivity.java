package com.cjq.androidx.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.cjq.androidx.R;
import com.cjq.androidx.bean.Employee;
import com.cjq.androidx.databinding.ActivityStreamDemoBinding;
import java.util.Arrays;
import java.util.List;

/**
 * java.util.Stream 表示能应用在一组元素上一次执行的操作序列。Stream 操作分为中间操作或者最终操作两种，最终操作返回一特定类型的计算结果，
 * 而中间操作返回Stream本身，这样就可以将多个操作依次串起来。
 * Stream 的创建需要指定一个数据源，比如 java.util.Collection的子类，List或者Set， Map不支持。
 * Stream的操作可以串行stream()执行或者并行parallelStream()执行。
 */
public class StreamDemoActivity extends BigBaseActivity {
    private ActivityStreamDemoBinding mViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViews = DataBindingUtil.setContentView(this, R.layout.activity_stream_demo);
        mViews.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int vId = v.getId();
        switch (vId) {
            case R.id.btnForEach:
                forEachTest();
                break;
            case R.id.btnLoadHtm:
                filterTest();
                break;
        }
    }

    public static void main(String[] args) {
        countTest();
    }

    //forEach 循环
    private static void forEachTest() {
        // 你不鸟我,我也不鸟你
        List<String> list = Arrays.asList("You", "don't", "bird", "me", ",", "I", "don't", "bird", "You");
        //java8 之前的循环方式
        for (String item : list) {
            System.out.println(item);
        }
        Stream.ofNullable(list).forEach(item -> System.out.println(item));
        Stream.ofNullable(list).forEach(System.out::println);
    }

    //filter过滤
    private static void filterTest() {
        List<Employee> employeeList = Arrays.asList(
                new Employee(1L, "andy", 28),
                new Employee(1L, "lisa", 18),
                new Employee(1L, "memory", 17));
        Stream.ofNullable(employeeList).filter(item->item.age>18).forEach(item->System.out.println(item));
    }

    //map 映射
    private static void mapTest() {
        List<Employee> employeeList = Arrays.asList(
                new Employee(1L, "andy", 28),
                new Employee(1L, "lisa", 18),
                new Employee(1L, "memory", 17));
        Stream.ofNullable(employeeList).filter(item->item.age>18).map(item->item.userName="sdfsdf").forEach(item->System.out.println(item));
    }
    //flatMap 多个列表操作
    private static void flatMapTest() {
        List<Integer> a = Arrays.asList(1, 2, 3);
        List<Integer> b = Arrays.asList(4, 5, 6);
        // <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper)
        List<List<Integer>> collect = Stream.of(a, b).collect(Collectors.toList());
        // [[1, 2, 3], [4, 5, 6]]
        System.out.println(collect);
        // 将多个集合中的元素合并成一个集合
        List<Integer> mergeList = Stream.of(a, b).flatMap(list -> Stream.of(list)).collect(Collectors.toList());
        // [1, 2, 3, 4, 5, 6]
        System.out.println(mergeList);
    }

    // sort 排序
    private static void sortTest(){
        List<String> list = Arrays.asList("c", "e", "a", "d", "b");
        // Stream<T> sorted(Comparator<? super T> comparator);
        // int compare(T o1, T o2);
        Stream.ofNullable(list).sorted((s1, s2) -> s1.compareTo(s2)).forEach(System.out::print);
    }


    // distinct 去重
    private static void distinctTest(){
        Stream<String> stream = Stream.of("know", "is", "know", "noknow", "is", "noknow");
        stream.distinct().forEach(System.out::println); // know is noknow

        System.out.println("next:");
        List<String> list = Arrays.asList("know", "is", "know", "noknow", "is", "noknow");
        Stream.ofNullable(list).distinct().forEach(System.out::println);
    }

    // count 总数量
    private static void countTest(){
        List<String> list = Arrays.asList("know", "is", "know", "noknow", "is", "noknow");
        long count = Stream.ofNullable(list).count();
        System.out.println(count);
    }

    //min max
    private static void minMaxTest(){
        List<String> list = Arrays.asList("1", "2", "3", "4", "5");
        // Optional<T> min(Comparator<? super T> comparator);
        Optional<String> optionalMin = Stream.ofNullable(list).min((a, b) -> a.compareTo(b));
        String valueMin = optionalMin.get();
        System.out.println(valueMin);

        Optional<String> optionalMax = Stream.ofNullable(list).max((a, b) -> a.compareTo(b));
        String valueMax = optionalMax.get();
        System.out.println(valueMax);
    }

    //skip
    private static void skipTest(){
        List<String> list = Arrays.asList("a", "b", "c", "d", "e");
        // Stream<T> skip(long n)
        Stream.ofNullable(list).skip(2).forEach(System.out::println);  // c、d、e
    }

    // limit
    private static void limitTest(){
        List<String> list = Arrays.asList("a", "b", "c", "d", "e");
        Stream.ofNullable(list).skip(2).limit(2).forEach(System.out::println);    // c、d
    }
}
