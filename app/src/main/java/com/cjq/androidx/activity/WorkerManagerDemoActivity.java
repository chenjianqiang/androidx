package com.cjq.androidx.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityWorkerDemoBinding;
import com.cjq.androidx.worker.PictureWorker;

public class WorkerManagerDemoActivity extends BigBaseActivity implements View.OnClickListener{
    private ActivityWorkerDemoBinding mBindingView;
    OneTimeWorkRequest oneTimeWorkRequest;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBindingView = DataBindingUtil.setContentView(this, R.layout.activity_worker_demo);
        mBindingView.setOnClickListener(this);
    }

    private void startWorker(){
        oneTimeWorkRequest = new OneTimeWorkRequest.Builder(PictureWorker.class).build();
        WorkManager.getInstance(WorkerManagerDemoActivity.this).enqueue(oneTimeWorkRequest);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_as:
                startWorker();
                break;
        }
    }
}
