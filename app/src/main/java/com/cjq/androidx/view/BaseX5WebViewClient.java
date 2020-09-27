package com.cjq.androidx.view;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class BaseX5WebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String s) {
        webView.loadUrl(s);
        return true;
    }

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        sslErrorHandler.proceed();
    }
}
