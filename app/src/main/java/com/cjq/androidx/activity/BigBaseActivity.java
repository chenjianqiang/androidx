package com.cjq.androidx.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * 设计一个超级父类
 * 囊括所有界面的通用功能，方便日后统一管理
 * 1.权限
 */
public abstract class BigBaseActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "BigBaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(this.getClass().getSimpleName());
        Log.d(TAG,"onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onClick(View v) {
        //这里监控app里面Activity上所有的点击，用于埋点或者其他用，v的Id每个业务界面要不一样才行
        // 对于自定义View里面的点击，在BaseView里面拦截
        processViewClick(v);
    }

    private void processViewClick(View v){
        ToastUtils.showShort("BigBaseActivity 检测到点击");
        Activity activity = ActivityUtils.getActivityByContext(v.getContext());
        if(activity instanceof AopDemoActivity){
            Log.e("BigBaseActivity","你是在 AopDemoActivity 点击的是某个按钮 ");
        }else if(activity instanceof WorkerManagerDemoActivity){
            Log.e("BigBaseActivity","你是在 WorkerManagerDemoActivity 点击的是某个按钮 ");
        }
    }
}
