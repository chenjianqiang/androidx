package com.cjq.androidx.web;

import android.text.TextUtils;

import com.cjq.androidx.MyApplication;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author cjq on 2019/1/17
 * 拦截器
 * 拦截器是OkHttp中提供一种强大机制，它可以实现网络监听、请求以及响应重写、请求失败重试等功能。下面举一个简单打印日志的栗子，此拦截器可以打印出网络请求以及响应的信息。
 * 检查参数是否异常，如异常可重新组装参数发起请求
 */
public class ApiInterceptor implements Interceptor {
    private String[] noFarmIdPathSegments = new String[]{
            "m/security/noticeRemind/",
            "/m/security/login"
    };
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = "";
        Request request = chain.request();
        HttpUrl url = request.url();
        Headers.Builder headerBuilder = request.headers()
                .newBuilder()
                .add("from", "Android");
        if (!TextUtils.isEmpty(token)) {
            headerBuilder.add("_gt", token);
        }
        headerBuilder.add("X-Requested-With", "XMLHttpRequest");
        headerBuilder.add("Connection:Closed");
        Headers newHeaders = headerBuilder.build();

        HttpUrl.Builder urlBuilder = url.newBuilder();
        /*int farmId = 888;
        if (isNeedFarmId(url)) {
            urlBuilder.addQueryParameter("farmId", farmId + "");
        }*/

        HttpUrl httpUrl = urlBuilder.build();
        Request newRequest = request.newBuilder().headers(newHeaders).url(httpUrl).build();
        return chain.proceed(newRequest);
    }

    private boolean isNeedFarmId(HttpUrl url) {
        for (String path : noFarmIdPathSegments) {
            if (url.toString().contains(path)) {
                return false;
            }
        }
        return true;
    }
}

