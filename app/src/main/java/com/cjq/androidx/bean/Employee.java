package com.cjq.androidx.bean;

public class Employee {
    public long id;
    public String userName;
    public String phone;
    public int age;

    public Employee(long id,String userName,int age){
        this.id = id;
        this.userName = userName;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                '}';
    }
}
