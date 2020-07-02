package com.cjq.androidx.activity;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.bean.UserInfo;
import com.cjq.androidx.databinding.ActivityDatabindingDemoBinding;
import com.cjq.androidx.tools.filter.TextInputTextWatcher;

/**
 * 如何进行单向绑定？
 * 1、UserInfo继承自BaseObservable
 * 2、为UserInfo中需要绑定的get方法添加注解@Bindable，set方法添加notifyPropertyChanged(BR.loginTime);
 * 3、如要进行双向绑定，再单向绑定基础上添加等号，如android:text="@={user.pwd}"
 *
 * 数据绑定又包括单向和双向
 * 单向绑定即当UserInfo数据发生改变时，控件(demo中的login_time_tv)会自动更新数据
 * 双向绑定常用于输入框,ListView等，当控件里面的输入数据变更时，对象数据(user.getName()+","+user.getPwd())也同步更新
 */
public class DataBindingDemoActivity extends BigBaseActivity {
    private static final String TAG = "DataBindingDemoActivity";
    private UserInfo user;
    private ActivityDatabindingDemoBinding binding;
    private Time mTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_databinding_demo);

        binding.nameEt.addTextChangedListener(new TextInputTextWatcher());

        mTime = new Time();
        user = new UserInfo("--","");
        binding.setUser(user);
        binding.setActivity(this);
    }

    public void userLogin(View v){
        mTime.setToNow();
        int year = mTime.year;
        int month = mTime.month+1;
        int day = mTime.monthDay;
        int hour = mTime.hour;
        int minute = mTime.minute;
        // 双向绑定
        Log.d(TAG, "userLogin: "+user.getName()+","+user.getPwd());
        ToastUtils.showLong(user.getName()+"登录中，这里的name是从User对象读取，并非从输入框读取,因为双向绑定，输入框里的数据变了，User对象的数据也自动同步更新");
        // 单向绑定
        user.setLoginTime("上次登录时间:"+year+"年"+month+"月"+day+"日"+hour+":"+minute);
    }
}
