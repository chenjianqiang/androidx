package com.cjq.androidx.activity;

import android.Manifest;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityTtsDemoBinding;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.Locale;

public class TTSDemoActivity extends BigBaseActivity {
    private ActivityTtsDemoBinding mView;
   // private String ttsMsg = "新一轮强降雨天气过程再次拉开帷幕，预计从今天开始一直到16日，西南地区东部、江汉、江淮、江南北部等地将再次遭遇强降雨过程侵袭，部分地区有大到暴雨，局地大暴雨，最强降雨时段为14-15日。气温方面，副高的“威力”依然不减，江南、华南的不少地区近几天炎热又将升级，高温范围也进一步扩大。";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this,R.layout.activity_tts_demo);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5f0c0044");
        play();
        mView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tts_start:
                ToastUtils.showShort("开始");
                String ttsMsg = mView.ttsMsg.getText().toString();
                if(StringUtils.isTrimEmpty(ttsMsg)){
                    return;
                }
                tts.speak(ttsMsg,TextToSpeech.QUEUE_ADD,null);
                break;
            case R.id.tts_stop:
                ToastUtils.showShort("停止TTS");
                stopTTS();
                break;
        }
    }

    private TextToSpeech tts;
    private void play() {
        tts = new TextToSpeech(this,new listener());
    }
    private class listener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                //设置播放语言
                int result = tts.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ToastUtils.showShort("不支持TTS");
                } else if (result == TextToSpeech.LANG_AVAILABLE) {
                    //初始化成功之后才可以播放文字
                    //否则会提示“speak failed: not bound to tts engine
                    //TextToSpeech.QUEUE_ADD会将加入队列的待播报文字按顺序播放
                    //TextToSpeech.QUEUE_FLUSH会替换原有文字
                    ToastUtils.showShort("支持TTS");
                }

            } else {
                Log.e("TAG", "初始化失败");
            }

        }

    }

    public void stopTTS() {
        if (tts != null) {
            tts.shutdown();
            tts.stop();
            tts = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTTS();
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE
        };
    }
}
