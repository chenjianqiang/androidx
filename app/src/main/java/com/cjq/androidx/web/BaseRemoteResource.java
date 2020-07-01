package com.cjq.androidx.web;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseRemoteResource<Result> extends NetworkResource<Result, Result> {

    private Result remoteData;

    @Override
    protected void saveCallResult(Result item) {
        remoteData = item;
    }

    @Override
    protected boolean shouldFetch(@Nullable Result data) {
        return true;
    }

    @NonNull
    @Override
    protected LiveData<Result> loadFromDb() {
        return AbsentLiveData.create();
    }

    @Override
    protected LiveData<Result> makeResultData(LiveData<Result> dbSource) {
        MutableLiveData<Result> result = new MutableLiveData<>();
        result.setValue(remoteData);
        return result;
    }

    @Override
    protected Type getRequestType() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
