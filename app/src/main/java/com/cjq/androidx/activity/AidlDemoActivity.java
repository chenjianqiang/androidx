package com.cjq.androidx.activity;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.android.internal.telephony.ITelephony;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityAidlDemoBinding;

import java.lang.reflect.Method;

public class AidlDemoActivity extends BigBaseActivity{
    private ActivityAidlDemoBinding mView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_aidl_demo);
        mView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        switch (id){
            case R.id.callByITelephony:
                callByITelephony();
                break;

        }
    }

    private boolean callByITelephony(){
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService",String.class);
            IBinder iBinder =  (IBinder)method.invoke(null, new Object[]{TELEPHONY_SERVICE});
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.dial("10010");
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
