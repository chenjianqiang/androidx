package com.cjq.androidx.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.annimon.stream.Stream;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityBlankjDemoBinding;
import com.cjq.androidx.databinding.ActivitySpanStringDemoBinding;
import com.cjq.androidx.tools.SpanStringBuilder;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import static android.Manifest.permission.READ_PHONE_STATE;


public class BlankjCodeDemoActivity extends AppCompatActivity {
    private ActivityBlankjDemoBinding mBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_blankj_demo);
        mBinding.setOnClickListener(this::onViewClick);
        mBinding.tv.setText(getInfoByBlankj());
    }

    private void onViewClick(View view) {
        switch (view.getId()){
            case R.id.clear_cache:
                deleteCacheFiles();
                mBinding.tv.setText(getInfoByBlankj());
                break;
        }
    }


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

        stringBuffer.append("cache路径缓存已使用大小:");
        stringBuffer.append(FileUtils.getSize(Utils.getApp().getCacheDir()));
        stringBuffer.append("\n");

        stringBuffer.append("屏幕分辨率:");
        stringBuffer.append(ScreenUtils.getAppScreenWidth() +" x " + ScreenUtils.getAppScreenHeight());
        stringBuffer.append("\n");

        stringBuffer.append("app内缓存大小:");
        stringBuffer.append(CacheDiskUtils.getInstance().getCacheSize());
        stringBuffer.append("\n");

        stringBuffer.append("app内缓存个数:");
        stringBuffer.append(CacheDiskUtils.getInstance().getCacheCount());
        stringBuffer.append("\n");
        stringBuffer.append("手机存储空间计算\n");

        stringBuffer.append("getFsTotalSize(Environment.getExternalStorageDirectory()):"+Environment.getExternalStorageDirectory().getAbsolutePath()+":");
        stringBuffer.append(getFormatSize(FileUtils.getFsTotalSize(Environment.getExternalStorageDirectory().getAbsolutePath())));
        stringBuffer.append("\n");

        stringBuffer.append("getFsAvailableSize Environment.getExternalStorageDirectory():"+Environment.getExternalStorageDirectory().getAbsolutePath()+":");
        stringBuffer.append(getFormatSize(FileUtils.getFsAvailableSize(Environment.getExternalStorageDirectory().getAbsolutePath())));
        stringBuffer.append("\n");
        stringBuffer.append("手机存储空间计算\n");

        stringBuffer.append("getFsTotalSize(Environment.getDataDirectory()):"+Environment.getDataDirectory().getAbsolutePath()+":");
        stringBuffer.append(getFormatSize(FileUtils.getFsTotalSize(Environment.getDataDirectory().getAbsolutePath())));
        stringBuffer.append("\n");

        stringBuffer.append("getFsAvailableSize Environment.getDataDirectory():"+Environment.getDataDirectory().getAbsolutePath()+":");
        stringBuffer.append(getFormatSize(FileUtils.getFsAvailableSize(Environment.getDataDirectory().getAbsolutePath())));
        stringBuffer.append("\n");

        return stringBuffer.toString();
    }

    private static String getFormatSize(long size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
    //根据文件创建日期由旧到新删除直至符合缓存限值
    public static void deleteCacheFiles(){
        List<File> allFilesInCacheDir = FileUtils.listFilesInDir(Utils.getApp().getCacheDir(),true);
        List<File> allFilesInCacheDirSorted =  Stream.of(allFilesInCacheDir).filter(file->file != null && file.isFile()).sorted((o1, o2) -> {
            if(o1.lastModified() > o2.lastModified()) {
                return 1;
            } else if(o1.lastModified() < o2.lastModified()) {
                return -1;
            } else {
                return 0;
            }
        }).toList();
        for (int i = 0; i < allFilesInCacheDirSorted.size(); i++) {
            File file = allFilesInCacheDirSorted.get(i);
                FileUtils.delete(file);
        }
    }

}
