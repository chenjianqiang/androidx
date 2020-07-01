package com.cjq.androidx.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;

public class BindingAdapters {

    @BindingAdapter(value = {"img", "isRound", "radius", "errorImg"}, requireAll = false)
    public static void setImg(ImageView iv, String path, boolean isRound, int radius, String errorImgUrl) {
        if (path == null) {
            return;
        }
        String url = path;
        int width = iv.getWidth();
        if (width == 0) {
            width = SizeUtils.dp2px(100);
        }
        Glide.with(iv).load(url).into(iv);
    }
}
