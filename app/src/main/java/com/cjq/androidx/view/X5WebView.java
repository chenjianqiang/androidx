package com.cjq.androidx.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.annimon.stream.Stream;
import com.cjq.androidx.interfaces.IFileChooser;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebSettings;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Map;

public class X5WebView extends WebView implements LifecycleObserver {
    private IFileChooser fileChooser;

    private OnX5ScrollChangedListener mOnX5ScrollChangedListener;

    public X5WebView(Context context) {
        super(context);
        initWebViewSettings();
    }

    public X5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initWebViewSettings();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(false);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        IX5WebSettingsExtension extension = getSettingsExtension();
        if (extension != null) {
            extension.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(0);
        }
        setWebViewClient(new BaseX5WebViewClient());
        setWebChromeClient(new BaseX5WebChromeClient(this));

        if (getContext() instanceof LifecycleOwner) {
            ((LifecycleOwner) getContext()).getLifecycle().addObserver(this);
        }
    }

    /**
     * @param client
     * @deprecated use {@link #setWebViewClient(BaseX5WebViewClient)} instead
     */
    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
    }

    public void setWebViewClient(BaseX5WebViewClient client) {
        super.setWebViewClient(client);
    }

    /**
     * 加载链接，参数会被自动encode
     *
     * @param url
     * @param args url 参数集合
     */
    public void loadUrl(@NonNull String url, @Nullable Bundle args) {
        this.loadUrl(buildUrl(url, args));
    }

    /**
     * @param url
     * @param headers
     */
    @Override
    public void loadUrl(String url, Map<String, String> headers) {
        super.loadUrl(url, headers);
    }

    private String buildUrl(String url, @Nullable Bundle params) {
        if (params == null) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Stream.of(params.keySet()).forEach(key -> {
            String value = String.valueOf(params.get(key));
            if (!TextUtils.isEmpty(value)) {
                builder.appendQueryParameter(key, value);
            }
        });
        return builder.build().toString();
    }

    /**
     * @param client
     * @deprecated use {@link #setWebChromeClient(BaseX5WebChromeClient)} instead
     */
    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    public void setWebChromeClient(BaseX5WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    public void addJavascriptInterface(Object o) {
        super.addJavascriptInterface(o, "AgriMAP");

        // 添加Android兼容模式，适配老逻辑，后续跟前端确认统一 TODO
        super.addJavascriptInterface(o, "android");
    }

    public void setFileChooser(IFileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    public IFileChooser getFileChooser() {
        return fileChooser;
    }

    @Override
    public void destroy() {
        WebChromeClient webChromeClient = getWebChromeClient();
        if (webChromeClient instanceof BaseX5WebChromeClient) {
            ((BaseX5WebChromeClient) webChromeClient).onWebViewDestroy();
        }
        super.destroy();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        this.destroy();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnX5ScrollChangedListener != null) {
            mOnX5ScrollChangedListener.onScrollChanged(l, t, oldl, oldt, this);
        }
    }

    /**
     * webview 滑动监听
     *
     * @param mOnX5ScrollChangedListener
     */
    public void setOnX5ScrollChangedListener(OnX5ScrollChangedListener mOnX5ScrollChangedListener) {
        this.mOnX5ScrollChangedListener = mOnX5ScrollChangedListener;
    }

    public interface OnX5ScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt, X5WebView view);
    }
}
