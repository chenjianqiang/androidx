package com.cjq.androidx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cjq.androidx.R;
import com.cjq.androidx.bean.DailyPicture;
import com.cjq.androidx.bean.ImageEntityGroup;
import com.cjq.androidx.bean.ImagesBean;
import com.cjq.androidx.databinding.AdapterDailyPictureViewBinding;
import com.cjq.androidx.interfaces.OnItemClickListener;

public class DailyPictureViewAdapter extends RecyclerView.Adapter<DailyPictureViewAdapter.DailyPictureViewHolder> {
    private Context context;

    private DailyPicture dailyPicture;
    private OnItemClickListener onItemClickListener;

    public DailyPictureViewAdapter(Context context,DailyPicture dailyPicture,OnItemClickListener onItemClickListener){
        this.context = context;
        this.dailyPicture = dailyPicture;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDailyPicture(DailyPicture dailyPicture) {
        this.dailyPicture = dailyPicture;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DailyPictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(context).inflate(R.layout.adapter_daily_picture_view,parent,false);
        AdapterDailyPictureViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.adapter_daily_picture_view,parent,false);
        binding.setOnItemClickListener(onItemClickListener);
        return new DailyPictureViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyPictureViewHolder holder, int position) {
        if(position == 0){
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            holder.binding.getRoot().setLayoutParams(layoutParams);
        }
        ImagesBean imagesBean = dailyPicture.getImages().get(position);
        holder.binding.setImage(imagesBean);
    }

    @Override
    public int getItemCount() {
        if(dailyPicture != null){
            if(dailyPicture.getImages() != null){
                return dailyPicture.getImages().size();
            }
        }
        return 0;
    }

    public static class DailyPictureViewHolder extends RecyclerView.ViewHolder{
        private AdapterDailyPictureViewBinding binding;
        public DailyPictureViewHolder(AdapterDailyPictureViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
