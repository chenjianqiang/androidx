package com.cjq.androidx.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityAopDemoBinding;


public class AopDemoActivity extends AppCompatActivity {
    private ActivityAopDemoBinding mView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_aop_demo);

    }
}
