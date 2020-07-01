package com.cjq.androidx.viewmodel;

import androidx.lifecycle.LiveData;

import com.cjq.androidx.bean.DailyPicture;
import com.cjq.androidx.bean.ImageEntityGroup;
import com.cjq.androidx.web.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DailyPictureService {

    @GET("HPImageArchive.aspx")
    Call<DailyPicture> getImageData(
            @Query("format") String js,
            @Query("idx") int idx,
            @Query("n") int n);

    @GET("HPImageArchive.aspx")
    LiveData<DailyPicture> getImagesDataLiveData(
            @Query("format") String js,
            @Query("idx") int idx,
            @Query("n") int n);


}
