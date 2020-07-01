package com.cjq.androidx.web;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.annotation.Nullable;

import retrofit2.HttpException;
import retrofit2.Response;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ApiResponse<T> {

    public T data;
    public String message;
    public String identifier;
    public String uri;
    public int status;

    /**
     * 请求中发生异常
     *
     * @param throwable 异常
     * @param <T>       内容
     * @return 包装之后的 api response
     */
    public static <T> ApiResponse<T> error(Throwable throwable) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.status = 500;
        if (ApiCenter.debug) {
            apiResponse.message = throwable.getMessage();
        } else {
            if (throwable instanceof UnknownHostException) {
                apiResponse.message = "网络连接失败";
            } else if (throwable instanceof SocketTimeoutException) {
                apiResponse.message = "网络连接超时";
            } else {
                apiResponse.message = "服务异常";
            }
        }
        return apiResponse;
    }

    public static <T> ApiResponse<T> create(Response<ApiResponse<T>> response) {
        if (response.isSuccessful()) {
            return response.body();
        }
        // http 响应码非 200
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.status = response.code();
        if (ApiCenter.debug) {
            apiResponse.message = response.message();
        } else {
            apiResponse.message = "服务异常";
        }
        return apiResponse;
    }

    @NonNull
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public boolean isSuccessful() {
        //return true;
        return (status >= 200 && status < 300) || status == 0;
    }
}
