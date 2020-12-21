package com.cjq.androidx.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ScreenUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityImageTestBinding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static android.os.FileUtils.copy;

public class ImageTestActivity2 extends BigBaseActivity {
    private ActivityImageTestBinding mView;
    private Handler mHandler = new Handler();
    Integer[] resImg = {R.mipmap.abum1, R.mipmap.abum2, R.mipmap.abum3, R.mipmap.abum4, R.mipmap.abum5, R.mipmap.abum6, R.mipmap.abum7, R.mipmap.abum8};
    private int resIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_image_test);
        Log.e("vvvvvvvvv", "ImageTestActivity2 getScreen:" + ScreenUtils.getScreenWidth() + "-" + ScreenUtils.getScreenHeight());
        mView.setOnClickListener(this);

        Bitmap merge = mergeBitmap(getBitmapFromResource(R.mipmap.abum3), getBitmapFromResource(R.mipmap.abum4));
        mView.wrapHeightImg.setImageBitmap(merge);
    }

    private Bitmap getBitmapFromResource(int reourceId) {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), reourceId).copy(Bitmap.Config.ARGB_8888, true); //copy函数表示bitmap可编辑
        return originalBitmap;
    }

    //合成两张图片
    public Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth()+secondBitmap.getWidth(), firstBitmap.getHeight()+secondBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        // canvas.drawBitmap(secondBitmap, firstBitmap.getWidth(), 0, null);//并排放
        // canvas.drawBitmap(secondBitmap, firstBitmap.getWidth(), firstBitmap.getHeight(), null);//对角放
        canvas.drawBitmap(secondBitmap, 0, firstBitmap.getHeight(), null);//垂直放
        return bitmap;
    }

    /**
     * 从本地或者网络读取图片，并转换为Bitmap图片
     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
     * <p>
     * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
     * B.本地路径:url="file://mnt/sdcard/photo/image.png";
     * C.支持的图片格式 ,png, jpg,bmp,gif等等
     * 说明：Constant.IO_BUFFER_SIZE 是一个常量而已，可以改成常数，比如2*1024，其实取决于你的图片大小，自己根据图片的大小自己设定吧。
     *
     * @param url
     * @return
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 2 * 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 2 * 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
