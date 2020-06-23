package com.cjq.androidx.database;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.cjq.androidx.database.entity.Employee;
import com.cjq.androidx.database.entity.NameTuple;

import java.util.Date;
import java.util.List;

/**
 * Room 将生成一个实现，在单个事务中将所有参数插入数据库中。
 */
@Dao
public interface EmployeeDao {
    //参数支持 传一个对象或者 对象的数组
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEmployee(Employee... employees);

    @Insert
    public void insertBothEmploy(Employee employee1,Employee employee2);

    @Query("SELECT * FROM employee")
    public List<Employee> getAllEmployee();

    @Query("SELECT * FROM employee where age<:maxAge")
    public List<Employee> getAllEmployByAge(int maxAge);


    @Query("SELECT * FROM employee where age between :minAge and :maxAge")
    public List<Employee> getAllEmployByAge(int minAge,int maxAge);

    //还可以在查询中传递多个参数或者多次引用它们。
    @Query("SELECT * FROM employee WHERE firstName LIKE :search " +
            "OR lastName LIKE :search")
    public List<Employee> findEmployeeWithName(String search);

    //在查询时，传递的参数还可以是一个集合。Room 知道参数何时是一个集合，并根据提供的参数数量在运行时自动展开。
    @Query("SELECT * FROM employee WHERE firstName IN (:firstNames)")
    public List<Employee> loadEmployeeFromRegions(List<String> firstNames);

    //返回列的子集
    //大多数情况下，我们可能只需要获取一个 Entity 中的几个 Field。这样可以节省宝贵的资源，并且可以更快速地完成查询。
    //只要结果列集合可以映射到返回的对象中，Room 允许返回任何基于 Java 的对象。例如这个NameTuple，可以创建以下普通的 Java 对象（plain old Java-based object, POJO）来获取用户的 first name 和 last name：
    @Query("SELECT firstName, lastName FROM employee WHERE firstName IN (:firstNames)")
    public List<NameTuple> loadNameTupleFromRegions(List<String> firstNames);

    @Query("SELECT firstName, lastName FROM employee")
    public List<NameTuple> loadFullName();

    //可观察的查询，返回LiveData数据
    //如果希望 App 的 UI 在数据发生变化时自动更新 UI，可以在查询方法中返回一个 LiveData 类型的值。Room 会产生所有必须的代码，用于在数据库发生变化时更新这个 LivaData 对象。
    @Query("SELECT firstName, lastName FROM employee WHERE firstName IN (:firstNames)")
    public LiveData<List<NameTuple>> loadNameTupleFromFirstName(List<String> firstNames);

    //查询的返回值可以是 Cursor 对象。
    @Query("SELECT * FROM employee WHERE age > :minAge LIMIT 5")
    public Cursor loadRawEmployeeOlderThan(int minAge);

    //装了类型转换器Converters以后，可以自由存储自定义类型和引用类型
    @Query("SELECT * FROM employee WHERE birthday BETWEEN :from AND :to")
    List<Employee> findUsersBornBetweenDates(Date from, Date to);

    @Update
    public void updateEmploy(Employee... employees);

    @Delete
    public void deleteEmployee(Employee... employees);

}
