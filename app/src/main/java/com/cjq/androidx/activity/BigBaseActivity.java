package com.cjq.androidx.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.interfaces.PermissionRequestInterface;
import com.cjq.androidx.tools.PermissionRequestHelper;
import com.cjq.androidx.tools.PermissionRequestUtil;

/**
 * 设计一个超级父类
 * 囊括所有界面的通用功能，方便日后统一管理
 * 1.权限
 */
public abstract class BigBaseActivity extends AppCompatActivity implements View.OnClickListener, PermissionRequestInterface {
    private final static String TAG = "BigBaseActivity";
    private PermissionRequestHelper mPermissionRequestHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(this.getClass().getSimpleName());
        Log.d(TAG,"onCreate");
        initPermissionRequest();
    }

    private void initPermissionRequest(){
        //初始化并发起权限申请
        mPermissionRequestHelper = new PermissionRequestHelper(this, this);
        mPermissionRequestHelper.requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionRequestHelper.requestPermissionsResult(requestCode,permissions,grantResults)){
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        return PermissionRequestUtil.PERMISSION_REQUEST_CODE;
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        ToastUtils.showShort("权限请求用户已经全部允许");
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        ToastUtils.showShort("权限有缺失");
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
