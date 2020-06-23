package com.cjq.androidx.database.entity;

import androidx.room.ColumnInfo;

public class NameTuple {

    @ColumnInfo(name = "firstName")
    public String firstName;

    @ColumnInfo(name = "lastName")
    public String lastName;
}
