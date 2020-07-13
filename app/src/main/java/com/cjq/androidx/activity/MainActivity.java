package com.cjq.androidx.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.cjq.androidx.R;
import com.cjq.androidx.view.FeatureView;

public class MainActivity extends ListActivity {
    private final DemoDetails[] demos = {
            new DemoDetails(R.string.common_app_demo, R.string.common_app_demo_desc, TTSDemoActivity.class),
            new DemoDetails(R.string.common_app_xunfei_demo, R.string.common_app_xunfei_demo_desc, XunfeiDemoActivity.class),
            new DemoDetails(R.string.common_app_demo_aop, R.string.common_app_demo_aop_desc, AopDemoActivity.class),
            new DemoDetails(R.string.common_app_demo_worker, R.string.common_app_demo_worker_desc, WorkerManagerDemoActivity.class),
            new DemoDetails(R.string.common_app_demo_room, R.string.common_app_demo_room_desc, RoomDemoActivity.class),
            new DemoDetails(R.string.common_app_mvvm_room, R.string.common_app_demo_mvvm_desc, MVVMDemoActivity.class),
            new DemoDetails(R.string.common_app_databinding, R.string.common_app_databinding_desc, DataBindingDemoActivity.class)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListAdapter adapter = new CustomArrayAdapter(
                this.getApplicationContext(), demos);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DemoDetails demo = (DemoDetails) getListAdapter().getItem(position);
        if (demo.activityClass != null) {
            startActivity(new Intent(this.getApplicationContext(),
                    demo.activityClass));
        }
    }

    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }
            DemoDetails demo = getItem(position);
            featureView.setTitleId(demo.titleId, demo.activityClass!=null);
            featureView.setDescriptionId(demo.descriptionId);
            return featureView;
        }
    }
    private static class DemoDetails {
        private final int titleId;
        private int descriptionId = -1;
        private final Class<? extends android.app.Activity> activityClass;

        public DemoDetails(int titleId, int descriptionId,
                           Class<? extends android.app.Activity> activityClass) {
            super();
            this.titleId = titleId;
            this.descriptionId = descriptionId;
            this.activityClass = activityClass;
        }
    }
}