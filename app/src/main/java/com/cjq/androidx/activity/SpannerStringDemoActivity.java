package com.cjq.androidx.activity;

import android.Manifest;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityAopDemoBinding;
import com.cjq.androidx.databinding.ActivitySpanStringDemoBinding;
import com.cjq.androidx.tools.SpanStringBuilder;


public class SpannerStringDemoActivity extends BigBaseActivity {
    private ActivitySpanStringDemoBinding mView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_span_string_demo);
       //mView.setOnClickListener(this);
        initSpan();
        mView.threeSpanDemo.setSelected(true); //开始滚动
        mView.oneSpanDemo.setSelected(true);//开始滚动
        mView.twoSpanDemo.setSelected(true);
    }


    private void initSpan(){
        SpanStringBuilder spanStringBuilder1 = new SpanStringBuilder(" 图标红色,字串大小20,加粗SSSSSSSS").color(R.color.colorPrimary).size(20)
                .click(R.color.text_1a1a1a,"图标红色,字串大小20,加粗",this::onSpanItemClick).drawable(R.mipmap.pot3);
        mView.oneSpanDemo.append(spanStringBuilder1.build());

        SpanStringBuilder spanStringBuilder2 = new SpanStringBuilder(" 图标紫色,字串大小15,不加粗SSSSSSSSSSSSSSSS")
                .color(R.color.colorAccent).size(15).click(R.color.text_1a1a1a,"图标紫色,字串大小15,不加粗",this::onSpanItemClick).drawable(R.mipmap.pot1);
        mView.oneSpanDemo.append(spanStringBuilder2.build());

        //解决点击不响应的问题
        mView.oneSpanDemo.setMovementMethod(LinkMovementMethod.getInstance());

        SpanStringBuilder spanStringBuilder5 = new SpanStringBuilder(" 图标绿色,字串大小20,加粗 ")
                .drawable(R.mipmap.pot4);
        mView.twoSpanDemo.append(spanStringBuilder5.build());

        SpanStringBuilder spanStringBuilder6 = new SpanStringBuilder(" 图标橙色,字串大小15,不加粗SSSSSSSSSSSSSSSS")
                .color(R.color.text_black).size(15).drawable(R.mipmap.pot2);
        mView.twoSpanDemo.append(spanStringBuilder6.build());
        mView.executePendingBindings();

    }

    public void onSpanItemClick(String type){
        ToastUtils.showShort(type);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_as:
                //ToastUtils.showShort("你点击的是某个按钮");
                Log.e("AopDemoActivity","你点击的是某个按钮");
                break;
        }
    }

}
