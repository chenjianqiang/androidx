package com.cjq.androidx.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityAopDemoBinding;
import com.cjq.androidx.databinding.ActivityMvvmDemoBinding;


public class MVVMDemoActivity extends AppCompatActivity {
    private ActivityMvvmDemoBinding mView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_mvvm_demo);
        mView.dailyPictureView.init(this);
    }
}
