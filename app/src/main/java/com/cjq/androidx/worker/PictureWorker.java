package com.cjq.androidx.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.blankj.utilcode.util.ToastUtils;

public class PictureWorker extends Worker {
    private final static String TAG = "PictureWorker";
    public final static String TAG_WORKER = "PictureWorker";
    //mWorkerParams 可用来接收外部传进来的参数
    private final WorkerParameters mWorkerParams;

    public PictureWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mWorkerParams = workerParams;
    }

    @NonNull
    @Override
    public Result doWork() {
        compress();
        // getInputData 获取外部传给Worker的参数
        int aa = getInputData().getInt("aa",0);
        String bb = getInputData().getString("bb");
        Log.d(TAG,"aa:"+aa + " bb:"+bb);

        // setOutputData 可以让 Worker 向外部传递数据结果
        Data outData = new Data.Builder().putInt("aa",aa+1).putString("bb",bb+1).build();
        return Result.success(outData);
    }

    private void compress(){
        Log.d(TAG,"compress:对图片进行压缩");
        ToastUtils.showShort("compress:对图片进行压缩");
    }

}
