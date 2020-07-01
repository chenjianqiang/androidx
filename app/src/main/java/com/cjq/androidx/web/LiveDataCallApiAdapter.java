package com.cjq.androidx.web;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * 将网络返回结果转成LiveData格式,因此如果服务端返回数据定义LiveData类型，就必须用此类转成LiveData
 * 返回结果有status,message,data格式
 * @param <R>
 */
public class LiveDataCallApiAdapter<R> implements CallAdapter<ApiResponse<R>,
        LiveData<ApiResponse<R>>> {
    private final Type responseType;

    LiveDataCallApiAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(@NonNull Call<ApiResponse<R>> call) {
        return new ApiResponseLiveData<>(call);
    }

    private class ApiResponseLiveData<T> extends LiveData<ApiResponse<T>> {
        private final DestroyObserver destroyObserver;
        private Call<ApiResponse<T>> call;
        AtomicBoolean started = new AtomicBoolean(false);
        private LifecycleOwner lifecycleOwner;

        ApiResponseLiveData(Call<ApiResponse<T>> call) {
            this.call = call;
            destroyObserver = new DestroyObserver();
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super ApiResponse<T>> observer) {
            super.observe(owner, observer);
            this.lifecycleOwner = owner;
            owner.getLifecycle().addObserver(destroyObserver);
        }

        @Override
        protected void onActive() {
            if (started.compareAndSet(false, true)) {
                call.enqueue(new Callback<ApiResponse<T>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<T>> call,
                                           @NonNull Response<ApiResponse<T>> response) {
                        removeDestroyObserver();
                        if (response.isSuccessful()) {
                            postValue(ApiResponse.create(response));
                        } else {
                            postValue(ApiResponse.error(null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<T>> call,
                                          @NonNull Throwable throwable) {
                        removeDestroyObserver();
                        postValue(ApiResponse.error(throwable));
                    }
                });
            }
        }

        private void removeDestroyObserver() {
            if (lifecycleOwner != null && destroyObserver != null) {
                lifecycleOwner.getLifecycle().removeObserver(destroyObserver);
            }
        }

        public class DestroyObserver implements LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                if (call != null && !call.isCanceled()) {
                    call.cancel();
                }
            }
        }
    }
}

