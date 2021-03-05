package com.cjq.androidx.activity;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.android.internal.telephony.ITelephony;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.bean.UserInfo;
import com.cjq.androidx.databinding.ActivityAidlDemoBinding;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AidlDemoActivity extends BigBaseActivity{
    private ActivityAidlDemoBinding mView;
    private static List<UserInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_aidl_demo);
        mView.setOnClickListener(this);
        ToastUtils.showShort("sdfsdf");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        switch (id){
            case R.id.callByITelephony:
                callByITelephony();
                break;
            case R.id.testThreadList:
                //printList();
                testError();
                break;

        }
    }

    private static void getWifiPwd(){
        new Thread(()->{
            int a = 0;
            while (a<100){
                UserInfo info = new UserInfo("pwd"+a,"name"+a);
                list.add(info);
                if(info.getPwd().equals("pwd3")){
                    list.remove(info);
                }
                a++;
                try {
                    Thread.currentThread().sleep(1000);
                }catch (Exception ex){
                    Log.e("AidlDemoActivity","kkkkkkkkk error");
                }
                if(info.getPwd().equals("pwd6")){
                    list.add(info);
                }
            }
        }).start();

    }
    public static void main(String[] args)  {
        //testError();
        getWifiPwd();
        printList();
    }
    private static void testError(){
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(2);
        Iterator<Integer> iterator = list.iterator();
        while(iterator.hasNext()) {
            Integer integer = iterator.next();
            if (integer == 2)
                list.remove(integer);
        }
    }

    private static void printList(){
        new Thread(()->{
            for(int i=0;i<list.size();i++){
                UserInfo info = list.get(i);
                info.setPwd("sdfsdf pwd"+i);
                if(info.getPwd().equals("pwd6")){
                    list.add(info);
                }
                try {
                    Thread.currentThread().sleep(800);
                }catch (Exception ex){
                    Log.e("AidlDemoActivity","kkkkkkkkk error2");
                }
            }
        }).start();
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
