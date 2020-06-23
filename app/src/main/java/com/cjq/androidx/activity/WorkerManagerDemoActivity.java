package com.cjq.androidx.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityWorkerDemoBinding;
import com.cjq.androidx.worker.PictureWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkerManagerDemoActivity extends BigBaseActivity {
    private ActivityWorkerDemoBinding mBindingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBindingView = DataBindingUtil.setContentView(this, R.layout.activity_worker_demo);
        mBindingView.setOnClickListener(this);

        WorkManager.getInstance(WorkerManagerDemoActivity.this).cancelAllWork();

        WorkManager.getInstance(WorkerManagerDemoActivity.this).getWorkInfosByTagLiveData(PictureWorker.TAG_WORKER).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                //多次执行，这个workInfos列表会列加，保存在本地数据库里面的,应该是用来查询任务的执行状态的，建议创建Request每次给不一样的tag，保持唯一性
                if(workInfos != null && workInfos.size() > 0) {
                    WorkInfo workInfo = workInfos.get(workInfos.size()-1);
                    int aa = workInfo.getOutputData().getInt("aa", 0);
                    String bb = workInfo.getOutputData().getString("bb");
                    ToastUtils.showShort("这里监听Worker的LiveData数据 aa:" + aa + " bb:" + bb + ",workInfos:"+workInfos.size());
                }
            }
        });
    }

    private void startWorker() {
        // Constraints 是给Worker执行添加约束条件，比如有网下才执行,低电量，充电，存储可用等
        // Constraints has Many other constraints are available, see the Constraints.Builder reference
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        //如果任务Worker需要重复执行，把 PictureWorker包装成 PeriodicWorkRequest，然后我们设置每隔12个小时压缩一次。然后我们在通过WorkManager把PeriodicWorkRequest加入到任务队列即可。
        // PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(PictureWorker.class,12, TimeUnit.HOURS)；

        //还可以 设置任务的执行前后顺序 (链式任务)

        //我们之前是根据任务的ID获取任务，我们也可以给任务设置标签Tag，通过Tag来获取任务。这样就不用定义全局成员变量oneTimeWorkRequest
        //OneTimeWorkRequest requestA = new OneTimeWorkRequest.Builder(AWorker.class).addTag("tag").setConstraints(myConstraints).build();
        //WorkManager.getInstance(this).getStatusesByTag("tag");

        //给任务传递参数, 后面也可以获取任务的返回结果。
        Data workerInputData = new Data.Builder().putInt("aa", 100).putString("bb", "200").build();

        //我们做一个测试分别在有网和没网的之后启动任务。
        //我们启动应用程序之后，关闭网络，任务不会执行，再打开网络的时候，任务执行了。
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(PictureWorker.class)
                .setConstraints(constraints)
                .addTag(PictureWorker.TAG_WORKER)
                .setInputData(workerInputData).build();
        WorkManager.getInstance(WorkerManagerDemoActivity.this).enqueue(oneTimeWorkRequest);

        //取消任务 WorkManager.getInstance(this).cancelWorkById(oneTimeWorkRequest.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_as:
                startWorker();
                break;
        }
    }
}
