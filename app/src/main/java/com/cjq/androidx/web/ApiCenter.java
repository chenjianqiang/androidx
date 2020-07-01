package com.cjq.androidx.web;

import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 不带证书
 * @author cjq on 2019/1/17
 */
public class ApiCenter {
    private static ApiCenter instance = null;
    public static final String API_HOST = "https://cn.bing.com/";
    public static boolean debug = true;
    private String baseUrl = API_HOST;
    private Retrofit.Builder builder;
    private HashMap<Class, Object> cacheServices = new HashMap<>();

    private ApiCenter(Context context) {
        builder = new Retrofit.Builder().client(new OkHttpClient.Builder().connectTimeout(5,
                TimeUnit.SECONDS)
                .addInterceptor(new ApiInterceptor())
                .addNetworkInterceptor(new StethoInterceptor()) //使用facebook 拦截查看连接
                .cache(new Cache(context.getCacheDir(), 10240 * 1024)).build())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static ApiCenter getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (ApiCenter.class) {
                if (instance == null) {
                    instance = new ApiCenter(context);
                }
            }
        }
        return instance;
    }

    public <T> T getService(Class<T> clazz) {
        Object cache = cacheServices.get(clazz);
        if (cache == null) {
            T service = builder.baseUrl(baseUrl).build().create(clazz);
            cacheServices.put(clazz, service);
            return service;
        } else {
            return (T) cache;
        }
    }
}
