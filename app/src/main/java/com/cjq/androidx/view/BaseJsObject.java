package com.cjq.androidx.view;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import androidx.annotation.Keep;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;

@Keep
public class BaseJsObject {
    protected X5WebView mX5WebView;

    public BaseJsObject() {
    }

    public BaseJsObject setX5WebView(X5WebView x5WebView) {
        this.mX5WebView = x5WebView;
        return this;
    }

    /**
     * 单图片点击大图显示
     *
     * @param url
     */
    @JavascriptInterface
    public void onImageClick(String url) {
        LogUtils.d("onImageClick", url);
        //ShowImgVideoActivity.start(true, 0, url);
    }

    /**
     * 多图片点击大图显示
     *
     * @param urls  图片url链接
     * @param index 当前显示的index
     */
    @JavascriptInterface
    public void onImageClick(String[] urls, int index) {
        LogUtils.d("onImageClick", urls, index);
        //ShowImgVideoActivity.start(true, index, urls);
    }

    /**
     * 多图片点击大图显示
     *
     * @param urls       图片url链接
     * @param index      当前显示的index
     * @param saveEnable 是否支持保存功能 0 不支持，非0，支持
     */
    @JavascriptInterface
    public void onImageClick(String[] urls, int index, int saveEnable) {
        LogUtils.d("onImageClick", urls, index);
        //ShowImgVideoActivity.start(saveEnable != 0, index, urls);
    }

    /**
     * 返回
     */
    @JavascriptInterface
    public void goBack() {
        if (mX5WebView == null) {
            throw new NullPointerException("for can go back, mX5WebView must be set");
        }
        Activity activity = ActivityUtils.getActivityByContext(mX5WebView.getContext());
        if (ActivityUtils.isActivityAlive(activity)) {
            activity.runOnUiThread(activity::onBackPressed);
        }
    }
}

