package com.cjq.androidx.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityImageTestBinding;
import com.cjq.androidx.tools.ImageScaleUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageTestActivity2 extends BigBaseActivity {
    private ActivityImageTestBinding mView;
    private Handler mHandler = new Handler();
    Integer[] resImg = {R.mipmap.abum1,R.mipmap.abum2,R.mipmap.abum3,R.mipmap.abum4,R.mipmap.abum5,R.mipmap.abum6,R.mipmap.abum7,R.mipmap.abum8};
    private int resIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this,R.layout.activity_image_test);
        Log.e("vvvvvvvvv","ImageTestActivity2 getScreen:"+ ScreenUtils.getScreenWidth()+"-"+ScreenUtils.getScreenHeight());
        mView.setOnClickListener(this);
        /*Log.e("vvvvvvvvv","ImageTestActivity2 Image000:"+mView.wrapHeightImg.getWidth()+"-"+mView.wrapHeightImg.getHeight());

        Bitmap bitmap = ImageUtils.getBitmap(R.mipmap.abum1);
        Log.e("vvvvvvvvv","ImageTestActivity2 bitmap:"+bitmap.getWidth()+"-"+bitmap.getHeight());
        mView.wrapHeightImg.setImageResource(R.mipmap.abum1);
        Log.e("vvvvvvvvv","ImageTestActivity2 Image:"+mView.wrapHeightImg.getWidth()+"-"+mView.wrapHeightImg.getHeight());*/

        //ImageScaleUtils.setImageViewMathParent(this,mView.wrapHeightImg,bitmap);
        changeImage();
    }


    private void changeImage(){
        mHandler.postDelayed(()->{
            Glide.with(mView.wrapHeightImg).load(resIndex).into(mView.wrapHeightImg);
            Log.v("vvvvvvvvv","resIndex:"+resIndex);
            resIndex++;
            if(resIndex > resImg.length){
                resIndex = 0;
            }
            changeImage();
        },1000);
    }


    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE,
        };
    }
}
