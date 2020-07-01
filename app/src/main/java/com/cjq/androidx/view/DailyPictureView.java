package com.cjq.androidx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.adapter.DailyPictureViewAdapter;
import com.cjq.androidx.bean.DailyPicture;
import com.cjq.androidx.bean.ImagesBean;
import com.cjq.androidx.databinding.ViewDailyPictureBinding;
import com.cjq.androidx.interfaces.OnItemClickListener;
import com.cjq.androidx.viewmodel.DailyPictureViewModel;
import com.cjq.androidx.web.Resource;

public class DailyPictureView extends BigBaseView implements OnItemClickListener<ImagesBean> {
    private ViewDailyPictureBinding mView;
    private DailyPictureViewModel dailyPictureViewModel;
    private DailyPictureViewAdapter dailyPictureViewAdapter;

    public DailyPictureView(@NonNull Context context) {
        super(context);
    }

    public DailyPictureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DailyPictureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(FragmentActivity activity) {
        dailyPictureViewModel = new ViewModelProvider(activity).get(DailyPictureViewModel.class);
        if(mView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mView = DataBindingUtil.inflate(inflater, R.layout.view_daily_picture, this, true);
            dailyPictureViewAdapter = new DailyPictureViewAdapter(getContext(), new DailyPicture(), this);
            mView.dailyRecyclerView.setAdapter(dailyPictureViewAdapter);

            dailyPictureViewModel.dailyPictureLiveData.observe(activity, this::onLoadDailyPicture);
            dailyPictureViewModel.loadDailyPicture();
        }
    }

    private void onLoadDailyPicture(DailyPicture resource) {
        if (resource != null) {
            dailyPictureViewAdapter.setDailyPicture(resource);
        }
    }

    @Override
    public void onItemClick(ImagesBean imagesBean) {
        ToastUtils.showShort(imagesBean.getCopyright());
    }
}
