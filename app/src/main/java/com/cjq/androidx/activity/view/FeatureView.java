package com.cjq.androidx.activity.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cjq.androidx.R;

/**
 * @author cjq on 2018/11/15
 */
public final class FeatureView extends FrameLayout {

    public FeatureView(Context context) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.feature, this);
    }

    public synchronized void setTitleId(int titleId) {
        ((TextView) (findViewById(R.id.title))).setText(titleId);
    }
    public synchronized void setTitleId(int titleId, boolean issub) {
        String title = this.getResources().getString(titleId);
        if (issub) {
            ((TextView) (findViewById(R.id.title))).setText("   ● "+title);
        } else{
            ((TextView) (findViewById(R.id.title))).setText(title);
        }

    }
    public synchronized void setDescriptionId(int descriptionId) {
        if(descriptionId != -1 && descriptionId != 0) {
            ((TextView) (findViewById(R.id.description))).setVisibility(View.VISIBLE);
            String description = this.getResources().getString(descriptionId);
            ((TextView) (findViewById(R.id.description))).setText("    "+description);
        }else{
            ((TextView) (findViewById(R.id.description))).setVisibility(View.GONE);
        }
    }

}


