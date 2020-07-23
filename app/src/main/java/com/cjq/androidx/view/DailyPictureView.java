package com.cjq.androidx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.adapter.DailyPictureViewAdapter;
import com.cjq.androidx.bean.DailyPicture;
import com.cjq.androidx.bean.ImagesBean;
import com.cjq.androidx.databinding.ViewDailyPictureBinding;
import com.cjq.androidx.interfaces.OnItemClickListener;
import com.cjq.androidx.viewmodel.DailyPictureViewModel;

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

    /**
     * 设置瀑布流布局中的某个item，独占一行、占一列、占两列、等等
     * @param mStaggeredGridLayoutManager
     * @param position 目标item所在的位置
     * @param TARGET_ITEM_TYPE 目标item的条目类型
     * @param parentView 该item的整个布局
     */
    /*private void setStaggeredItemSpanCount(StaggeredGridLayoutManager mStaggeredGridLayoutManager,int position,int TARGET_ITEM_TYPE,View parentView){
        int type = getItemViewType(position);
        if(type == TARGET_ITEM_TYPE){
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            parentView.setLayoutParams(layoutParams);
        }
    }*/

    /**
     * 设置网格布局，2列纵向显示(宽度均分，高度均分)
     * 特殊处：第一个元素占两列，其他元素占一列
     */
    private void initRecycleViewSpan(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ActivityUtils.getActivityByContext(getContext()),2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0?2:1;
            }
        });
        mView.dailyRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onItemClick(ImagesBean imagesBean) {
        ToastUtils.showShort(imagesBean.getCopyright());
    }
}
