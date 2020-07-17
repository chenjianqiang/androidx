package com.cjq.androidx.tools;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.interfaces.OnItemClickListener;

public class MyClickSpan<T> extends ClickableSpan {
    private OnItemClickListener<T> onItemClickListener;
    private int color;
    private T type;

    public MyClickSpan(int color,T t){
        this.color = color;
        this.type = t;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(@NonNull View widget) {
        ToastUtils.showShort("点击触发---");
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(type);
        }
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        //设置字体颜色
        ds.setColor(color);
        ds.setUnderlineText(false); //去掉下划线
    }
}
