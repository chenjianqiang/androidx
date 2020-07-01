package com.cjq.androidx.web;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * @author cjq on 2019/1/17
 */
public class LiveDataCallAdapterFactory extends CallAdapter.Factory {
    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != LiveData.class) {//如果service返回数据不需要是LiveData类型，这里直接返回null
            return null;
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        if (!(observableType instanceof ParameterizedType)) {
            return  new LiveDataCallAdapter<>(observableType);//返回结果不带data,message,status
        }

        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType != ApiResponse.class) {
           throw new IllegalArgumentException("type must be a resource");
        }
        return new LiveDataCallApiAdapter<>(observableType);//返回结果带data,message,status
    }

}
