package com.cjq.androidx.database.entity;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(primaryKeys = {"firstName","lastName"},tableName = "employee")
public class Employee {
    public int id;//这个id暂时没用

    //这样firstName和lastName的复合主键，只要他们都一样，就会认为是同一个，添加到数据库时会替换旧数据
    @NonNull
    public String firstName;
    @NonNull
    public String lastName;

    public int age;

    public Date birthday;

    public String account;

    public String avatarUrl;

    //默认情况下，Room为 Entity中每个Field 创建一列。如果在 Entity 中存在不需要持久化的 Field，可以给它们添加 @Ignore 注解。
    // 如果子类不需要持久化父类中的 Field，使用 @Entity 的 ignoredColumns 属性更为方便。
    @Ignore
    public Bitmap avatar;
}
