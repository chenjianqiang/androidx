package com.cjq.androidx.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.Utils;
import com.cjq.androidx.bean.DailyPicture;
import com.cjq.androidx.bean.ImageEntityGroup;
import com.cjq.androidx.web.ApiCenter;
import com.cjq.androidx.web.ApiResponse;
import com.cjq.androidx.web.BaseRemoteResource;
import com.cjq.androidx.web.Resource;

import java.util.Date;

public class DailyPictureRepository {
    private DailyPictureService mDailyPictureService;

    public DailyPictureRepository(){
        mDailyPictureService = ApiCenter.getInstance(Utils.getApp()).getService(DailyPictureService.class);
    }

    public LiveData<DailyPicture> getDailyPicture(String format, int idx, int n) {
        return mDailyPictureService.getImagesDataLiveData(format,idx,n);
    }
}
