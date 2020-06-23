package com.cjq.androidx.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.MyApplication;
import com.cjq.androidx.R;
import com.cjq.androidx.database.EmployeeDao;
import com.cjq.androidx.database.entity.Employee;
import com.cjq.androidx.database.entity.NameTuple;
import com.cjq.androidx.databinding.ActivityRoomDemoBinding;

import java.util.ArrayList;
import java.util.List;

public class RoomDemoActivity extends BigBaseActivity {
    private ActivityRoomDemoBinding mView;
    private EmployeeDao employeeDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_room_demo);
        mView.setOnClickListener(this);
        employeeDao = MyApplication.getMyDataBase().employeeDao();
    }

    private void productLargeEmployee() {
        long start = System.currentTimeMillis();
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Employee employee = new Employee();
            employee.firstName = "ken" + i;
            employee.lastName = "min" + i;
            employee.age = 20 + i;
            employeeList.add(employee);
        }
        employeeDao.insertEmployee(employeeList.toArray(new Employee[employeeList.size()]));
        Log.e("RoomDemoActivity", "cost:" + (System.currentTimeMillis() - start));
    }

    private void loadEmployee() {
        List<NameTuple> employees = employeeDao.loadFullName();
        if (employees != null && employees.size() > 0) {
            mView.setNameTuple(employees.get(employees.size() - 1));
        } else {
            ToastUtils.showShort("当前数据表是空的");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_add:
                productLargeEmployee();
                break;
            case R.id.tv_query:
                loadEmployee();
                break;
        }
    }

    //==================权限请求接口=================
    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };
    }
}
