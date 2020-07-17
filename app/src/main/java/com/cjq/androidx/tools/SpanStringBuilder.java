package com.cjq.androidx.tools;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import com.blankj.utilcode.util.Utils;
import com.cjq.androidx.interfaces.OnItemClickListener;

/**
 * 构建SpannableString类，可以定义内容，颜色，是否加粗，字体大小，图标，点击事件
 */
public class SpanStringBuilder {

    private SpannableString content;
    private int length = 0;

    public SpanStringBuilder(String contents){
        this.content = new SpannableString(contents);
        length = contents.length();
    }
    public SpanStringBuilder color(String color) {
        return color(Color.parseColor(color));
    }

    public SpanStringBuilder color(int color) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        content.setSpan(colorSpan, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpanStringBuilder style(int typeface) {
        StyleSpan styleSpan = new StyleSpan(typeface);
        content.setSpan(styleSpan, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpanStringBuilder size(int size) {
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(size, true);
        content.setSpan(span, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpanStringBuilder drawable(int drawable){
        Drawable drawable1 = Utils.getApp().getResources().getDrawable(drawable);
        drawable1.setBounds(0, 0, drawable1.getIntrinsicWidth(), drawable1.getIntrinsicHeight());
        MyImageSpan span = new MyImageSpan(drawable1);
        content.setSpan(span,0,1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }


    public SpanStringBuilder click(int color,String clickStrMsg,OnItemClickListener<String> onItemClickListener){
        MyClickSpan span = new MyClickSpan<String>(color,clickStrMsg);
        span.setOnItemClickListener(onItemClickListener);
        content.setSpan(span, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableString build() {
        return content;
    }

}
