package com.cjq.androidx.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.cjq.androidx.database.entity.Employee;

@Database(entities = {Employee.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class MyDataBase extends RoomDatabase {
    public abstract EmployeeDao employeeDao();
}
