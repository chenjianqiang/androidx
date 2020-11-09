package com.cjq.androidx.activity;

import android.Manifest;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivitySpanStringDemoBinding;
import com.cjq.androidx.tools.SpanStringBuilder;

import static android.Manifest.permission.READ_PHONE_STATE;


public class BlankjCodeDemoActivity extends BigBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText(getInfoByBlankj());
        setContentView(tv);
    }


    @RequiresPermission(READ_PHONE_STATE)
    private String getInfoByBlankj(){
        StringBuffer stringBuffer = new StringBuffer("");
        stringBuffer.append("app版本号:");
        stringBuffer.append(AppUtils.getAppVersionName());
        stringBuffer.append("\n");

        stringBuffer.append("获取设备系统版本号:");
        stringBuffer.append(DeviceUtils.getSDKVersionName());
        stringBuffer.append("\n");

        stringBuffer.append("设备 AndroidID:");
        stringBuffer.append(DeviceUtils.getAndroidID());
        stringBuffer.append("\n");

        stringBuffer.append("设备厂商:");
        stringBuffer.append(DeviceUtils.getManufacturer());
        stringBuffer.append("\n");

        stringBuffer.append("设备型号:");
        stringBuffer.append(DeviceUtils.getModel());
        stringBuffer.append("\n");

        stringBuffer.append("获取 IMSI 码:");
        stringBuffer.append(PhoneUtils.getIMSI());
        stringBuffer.append("\n");

        stringBuffer.append("屏幕分辨率:");
        stringBuffer.append(ScreenUtils.getAppScreenWidth() +" x " + ScreenUtils.getAppScreenHeight());
        stringBuffer.append("\n");

        return stringBuffer.toString();
    }


    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{Manifest.permission.READ_PHONE_STATE
        };
    }
}
