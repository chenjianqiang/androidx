package com.cjq.androidx.viewmodel;

import android.os.SystemClock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.cjq.androidx.bean.DailyPicture;
import com.cjq.androidx.web.Resource;

public class DailyPictureViewModel extends ViewModel {
    private DailyPictureRepository dailyPictureRepository;
    private MutableLiveData<Long> triggerDailyPicture = new MutableLiveData<>();
    public LiveData<DailyPicture> dailyPictureLiveData;

    public DailyPictureViewModel(){
        dailyPictureRepository = new DailyPictureRepository();
        dailyPictureLiveData = Transformations.switchMap(triggerDailyPicture,input->dailyPictureRepository.getDailyPicture("js",0,5));
    }

    public void loadDailyPicture(){
        triggerDailyPicture.setValue(SystemClock.uptimeMillis());
    }
}
