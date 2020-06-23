package com.cjq.androidx.database;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * 使用 Room 引用复杂数据，有时，希望将自定义的数据类型的值存储在数据库的单个列中。
 * Room 提供了在基本类型和盒式类型之间转换的功能，但不允许 Entity 之间的对象引用(如果放开，就会有延迟加载的问题，会造成UI主线程阻塞)。
 * 这个时候，使用类型转换器
 * 为了支持自定义类型，需要提供一个 TypeConverter，它将自定义类型转换为 Room 能够持久化的已知类型。
 */
public class Converters {
    //由于 Room 已经知道如何持久化 Long 对象，所以它能使用这个转换器来持久化 Data 类型的数据。
    //接下来，为 MyDatabase 添加 @TypeConverters 注解，以便 Room 能使用为 Entity 和 DAO 定义的转换器。
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
