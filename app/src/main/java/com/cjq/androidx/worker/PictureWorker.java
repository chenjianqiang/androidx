package com.cjq.androidx.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.blankj.utilcode.util.ToastUtils;

public class PictureWorker extends Worker {
    private final static String TAG = "PictureWorker";
    //mWorkerParams 用来接收外部传进来的参数
    private final WorkerParameters mWorkerParams;

    public PictureWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mWorkerParams = workerParams;
    }

    @NonNull
    @Override
    public Result doWork() {
        compress();
        return Result.success();
    }

    private void compress(){
        Log.d(TAG,"compress:对图片进行压缩");
        ToastUtils.showShort("compress:对图片进行压缩");
    }
}
