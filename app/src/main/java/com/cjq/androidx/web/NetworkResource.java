package com.cjq.androidx.web;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.util.ObjectsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.StringUtils;
import com.cjq.androidx.R;

import java.lang.reflect.Type;

import static com.cjq.androidx.MyApplication.EXECUTORS;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * 支持网络数据缓存，以及缓存有效期
 * @param <Result>
 * @param <Request>
 */
public abstract class NetworkResource<Result, Request> {

    private static final String TAG = "NetworkResource";
    protected MediatorLiveData<Resource<Result>> result = new MediatorLiveData<>();

    @MainThread
    protected NetworkResource() {
        result.setValue(Resource.loading(null));
        LiveData<Result> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                if (!fetchFromCache()) {
                    fetchFromNetwork(dbSource);
                }
            } else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    @MainThread
    private void setValue(Resource<Result> newValue) {
        if (!ObjectsCompat.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    private void fetchFromNetwork(final LiveData<Result> dbSource) {
        LiveData<ApiResponse<Request>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            if (response != null && response.isSuccessful()) {
                if (response.data == null ||
                        ((response.data instanceof Iterable) && !((Iterable) response.data).iterator().hasNext())) {
                    EXECUTORS.mainThread().execute(() -> {
                        // reload from disk whatever we had
                        result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)));
                    });
                } else {
                    EXECUTORS.diskIO().execute(() -> {
                        Request requestData = processResponse(response);
                        saveCallResultToCache(requestData);
                        saveCallResult(requestData);
                        EXECUTORS.mainThread().execute(() ->
                                {
                                    // we specially request a new live data,
                                    // otherwise we will get immediately last cached value,
                                    // which may not be updated with latest results received from network.
                                    LiveData<Result> source = makeResultData(dbSource);
                                    result.addSource(source, newData -> setValue(Resource.success(newData)));
                                }
                        );
                    });
                }
            } else {
                onFetchFailed();
                result.addSource(dbSource, newData -> {
                    String message = response == null ||
                            StringUtils.isTrimEmpty(response.message) ||
                            "null".equalsIgnoreCase(response.message)
                            ? StringUtils.getString(R.string.http_net_error_hint) : response.message;
                    setValue(Resource.error(message, newData));
                });
            }
        });
    }

    public LiveData<Resource<Result>> asLiveData() {
        return result;
    }

    protected LiveData<Result> makeResultData(LiveData<Result> dbSource) {
        return loadFromDb();
    }

    protected void onFetchFailed() {
    }

    @WorkerThread
    protected Request processResponse(ApiResponse<Request> response) {
        return response.data;
    }

    @WorkerThread
    protected abstract void saveCallResult(Request item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable Result data);

    @NonNull
    @MainThread
    protected abstract LiveData<Result> loadFromDb();

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<Request>> createCall();

    /**
     * 缓存数据存储键
     *
     * @return
     */
    @Nullable
    protected String fetchFromCacheKey() {
        return null;
    }

    /**
     * 缓存数据有效时间, 单位秒
     *
     * @return
     */
    protected int fetchFromCacheInterval() {
        return 0;
    }

    /**
     * 从缓存获取数据
     *
     * @return true if fetch success from cache，otherwise false
     */
    private boolean fetchFromCache() {
        String cacheKey = fetchFromCacheKey();
        int cacheInterval = fetchFromCacheInterval();
        if (cacheInterval == 0 || TextUtils.isEmpty(cacheKey)) {
            return false;
        }
        String jsonData = CacheDiskUtils.getInstance().getString(cacheKey);
        if (TextUtils.isEmpty(jsonData)) {
            return false;
        }
        try {
            Type type = getRequestType();
            if (type == null) {
                return false;
            }
            Request request = JSON.parseObject(jsonData, type);

            Log.d(TAG, "从缓存获取数据-> \n有效期：" + cacheInterval + "\n键："
                    + cacheKey + "\n数据:" + jsonData);
            saveCallResult(request);
        } catch (JSONException e) {
            // 这里如果缓存解析异常，也返回false，为了避免因为数据结构变化导致
            CacheDiskUtils.getInstance().remove(cacheKey);
            return false;
        }
        LiveData<Result> source = makeResultData(loadFromDb());
        result.addSource(source, newData -> {
            result.removeSource(source);
            setValue(Resource.success(newData));
        });
        return true;
    }

    /**
     * 将请求数据保存到缓存
     *
     * @param requestData
     */
    private void saveCallResultToCache(Request requestData) {
        String cacheKey = fetchFromCacheKey();
        int cacheInterval = fetchFromCacheInterval();
        if (cacheInterval == 0 || TextUtils.isEmpty(cacheKey)) {
            return;
        }
        String saveData = JSON.toJSONString(requestData);
        Log.d(TAG, "保存数据到缓存-> \n有效期：" + cacheInterval + "\n键：" + cacheKey
                + "\n数据:" + saveData);
        CacheDiskUtils.getInstance().put(cacheKey, saveData, cacheInterval);
    }

    protected Type getRequestType() {
        return null;
    }
}

