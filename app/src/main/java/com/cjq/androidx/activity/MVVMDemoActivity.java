package com.cjq.androidx.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityAopDemoBinding;
import com.cjq.androidx.databinding.ActivityMvvmDemoBinding;


public class MVVMDemoActivity extends BigBaseActivity {
    private ActivityMvvmDemoBinding mView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_mvvm_demo);
        mView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_as:
                //ToastUtils.showShort("你点击的是某个按钮");
                Log.e("AopDemoActivity","你点击的是某个按钮");
                break;
        }
    }

}
