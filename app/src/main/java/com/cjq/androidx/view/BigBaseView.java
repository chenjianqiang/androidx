package com.cjq.androidx.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.activity.AopDemoActivity;
import com.cjq.androidx.activity.WorkerManagerDemoActivity;

/**
 * 设计一个View超级父类
 * 用于所有自定义业务界面View的父类，方便日后统一管理
 * 1.点击
 */
public class BigBaseView extends FrameLayout implements View.OnClickListener {
    public BigBaseView(@NonNull Context context) {
        super(context);
    }

    public BigBaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BigBaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View v) {
        processViewClick(v);
    }

    private void processViewClick(View v){
        Activity activity = ActivityUtils.getActivityByContext(v.getContext());
        if(activity instanceof AopDemoActivity){
            Log.e("BigBaseActivity","你是在 AopDemoActivity 点击的是某个按钮 ");
        }else if(activity instanceof WorkerManagerDemoActivity){
            Log.e("BigBaseActivity","你是在 WorkerManagerDemoActivity 点击的是某个按钮 ");
        }
    }
}
