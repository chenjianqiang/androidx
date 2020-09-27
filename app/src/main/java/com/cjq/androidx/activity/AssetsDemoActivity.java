package com.cjq.androidx.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.bean.Farm;
import com.cjq.androidx.databinding.ActivityAssetsDemoBinding;
import com.cjq.androidx.tools.AssetsFileUtils;
import com.cjq.androidx.view.BaseJsObject;
import com.cjq.androidx.view.WebPageBuilder;

/**
 * 从assets里获取数据演示
 */
public class AssetsDemoActivity extends BigBaseActivity{
    private ActivityAssetsDemoBinding mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_assets_demo);
        mView.setOnClickListener(this);
    }

    private void toAnotherWeb() {
        new WebPageBuilder().setUrl("file:///android_asset/test.html")
                .setTitle("测试")
                .setEnableChooseImg(true)
                .setJsObject(new BaseJsObject() {
                    @Override
                    @JavascriptInterface
                    public void onImageClick(String url) {
                        ToastUtils.showShort("点击看大图");
                        super.onImageClick(url);
                    }

                    @JavascriptInterface
                    public void onCall(String num, String name) {
                        ToastUtils.showShort("webView hash:" + mX5WebView.hashCode());
                        ToastUtils.showShort("num:" + num + " name:"+name);
                    }
                })
                .setTitleIsBold(true)
                .buildPage();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int vId = v.getId();
        switch (vId){
            case R.id.btnParseObj:
                Farm farm = AssetsFileUtils.parseAssetJson("nitrogen_farm.json",Farm.class);
                ToastUtils.showShort(farm.toString());
                break;
            case R.id.btnLoadHtm:
                toAnotherWeb();
                break;
        }
    }
}
