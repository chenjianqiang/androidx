package com.cjq.androidx.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;

public class ImageScaleUtils {

    /**
     * 自适应图片的ImageView
     * 必须在主线程执行
     * @param context 上下文对象
     * @param image imageveiw 对象
     * @param bitmap 参数
     */
    public static void setImageViewMathParent(Context context,
                                              ImageView image, Bitmap bitmap) {
        //获得ImageView的参数类
        ViewGroup.LayoutParams vgl = image.getLayoutParams();
        if (bitmap == null) {
            return;
        }
        //获取bitmap的宽度
        float bitWidth = bitmap.getWidth();
        //获取bitmap的宽度
        float bithight = bitmap.getHeight();

        //计算出图片的宽高比，然后按照图片的比列去缩放图片
        float bitScalew = bitWidth / bithight;
        //如果是图片的宽大于高  则采用屏幕的五分之三设置图片的宽 高按照比例计算
        float imgWidth = ScreenUtils.getScreenWidth() * 3 / 5;
        //如果是图片的高大于宽  则采用屏幕的三分之一设置图片的高 宽按照比例计算
        float imgHight = ScreenUtils.getScreenHeight() * 1 / 3;
        //如果图片宽度大于高度
        if (bitWidth > bithight) {
            vgl.width = (int) imgWidth;
            vgl.height = (int) (imgWidth / bitScalew);
        } else {
            //当图片的高度大于宽度的
            vgl.width = (int) (imgHight * bitScalew);
            vgl.height = (int) imgHight;
        }
        //设置图片充满ImageView控件
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //等比例缩放
        image.setAdjustViewBounds(true);
        image.setLayoutParams(vgl);
        image.setImageBitmap(bitmap);
    }
}
