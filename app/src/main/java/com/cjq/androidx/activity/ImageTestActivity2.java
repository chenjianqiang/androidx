package com.cjq.androidx.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityImageTestBinding;
import com.cjq.androidx.tools.ImageScaleUtils;

public class ImageTestActivity2 extends BigBaseActivity {
    private ActivityImageTestBinding mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this,R.layout.activity_image_test);
        Log.e("vvvvvvvvv","ImageTestActivity2 getScreen:"+ ScreenUtils.getScreenWidth()+"-"+ScreenUtils.getScreenHeight());
        mView.setOnClickListener(this);
        Log.e("vvvvvvvvv","ImageTestActivity2 Image000:"+mView.wrapHeightImg.getWidth()+"-"+mView.wrapHeightImg.getHeight());

        Bitmap bitmap = ImageUtils.getBitmap(R.mipmap.abum1);
        Log.e("vvvvvvvvv","ImageTestActivity2 bitmap:"+bitmap.getWidth()+"-"+bitmap.getHeight());
        mView.wrapHeightImg.setImageResource(R.mipmap.abum1);
        Log.e("vvvvvvvvv","ImageTestActivity2 Image:"+mView.wrapHeightImg.getWidth()+"-"+mView.wrapHeightImg.getHeight());

        ImageScaleUtils.setImageViewMathParent(this,mView.wrapHeightImg,bitmap);
    }



    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE,
        };
    }
}
