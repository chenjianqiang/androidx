package com.cjq.androidx.tools;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.cjq.androidx.interfaces.PermissionRequestInterface;

/**
 * 自定义权限请求帮助类
 * @author cjq
 */
public class PermissionRequestHelper {
    private Activity mActivity;
    private PermissionRequestInterface mPermissionRequestInterface;

    public PermissionRequestHelper(@NonNull Activity activity, @NonNull PermissionRequestInterface permissionRequestInterface){
        mActivity = activity;
        mPermissionRequestInterface = permissionRequestInterface;
    }

    /**
     * 开始请求权限。
     * 方法内部已经对Android M 或以上版本进行了判断，外部使用不再需要重复判断。
     * 如果设备还不是M或以上版本，则也会回调到requestPermissionsSuccess方法。
     */
    public void requestPermissions(){
        String[] deniedPermissions = PermissionRequestUtil.getDeniedPermissions(mActivity, mPermissionRequestInterface.getPermissions());
        if(deniedPermissions != null && deniedPermissions.length > 0){
            PermissionRequestUtil.requestPermissions(mActivity, deniedPermissions, mPermissionRequestInterface.getPermissionsRequestCode());
        }else{
            mPermissionRequestInterface.requestPermissionsSuccess();
        }
    }

    /**
     * 在Activity中的onRequestPermissionsResult中调用
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return true 代表对该requestCode感兴趣，并已经处理掉了。false 对该requestCode不感兴趣，不处理。
     */
    public boolean requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == mPermissionRequestInterface.getPermissionsRequestCode()){
            boolean isAllGranted = true;//是否全部权限已授权
            for(int result : grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    isAllGranted = false;
                    break;
                }
            }
            if(isAllGranted){
                //已全部授权
                mPermissionRequestInterface.requestPermissionsSuccess();
            }else{
                //权限有缺失
                mPermissionRequestInterface.requestPermissionsFail();
            }
            return true;
        }
        return false;
    }
}

