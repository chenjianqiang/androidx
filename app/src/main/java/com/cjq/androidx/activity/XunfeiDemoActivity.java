package com.cjq.androidx.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityTtsDemoBinding;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

public class XunfeiDemoActivity extends BigBaseActivity {
    //https://www.jianshu.com/p/6e9cc56f080b
    private ActivityTtsDemoBinding mView;
    // 默认发音人
    private String voicer = "xiaoyan";
    private String TAG = "XunfeiDemoActivity";
    private boolean isPause;
    private boolean isComplete;

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private SpeechSynthesizer speechSynthesizer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this,R.layout.activity_tts_demo);
        mView.setOnClickListener(this);
        init();
    }

    private void init() {
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.d(TAG, "Xunfei InitListener init() code = " + i);
                if (i != ErrorCode.SUCCESS) {
                    ToastUtils.showShort("初始化失败："+i);
                } else {
                    // 初始化成功，之后可以调用startSpeaking方法
                    // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                    // 正确的做法是将onCreate中的startSpeaking调用移至这里
                    setParams();
                }

            }
        });
    }

    private void say(String text) {
        Log.d(TAG, "Xunfei say():"+speechSynthesizer.isSpeaking());
        if(speechSynthesizer.isSpeaking()){
            Log.e(TAG,"isSpeaking");
            return;
        }
        Log.d(TAG, "Xunfei startSpeaking");
        startSpeaking(text);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tts_start:
                ToastUtils.showShort("开始讯飞播报");
                String ttsMsg = mView.ttsMsg.getText().toString();
                if(StringUtils.isTrimEmpty(ttsMsg)){
                    return;
                }
                say(ttsMsg);
                break;
            case R.id.tts_stop:
                ToastUtils.showShort("停止讯飞播报");
                speechSynthesizer.stopSpeaking();
                break;
            case R.id.tts_pause:
                if(!isPause) {
                    mView.ttsPause.setText("继续播报");
                    speechSynthesizer.pauseSpeaking();
                }else{
                    mView.ttsPause.setText("暂停播报");
                    speechSynthesizer.resumeSpeaking();
                }
                break;
        }
    }

    private void startSpeaking(String text) {
        speechSynthesizer.startSpeaking(text, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                isComplete = false;
                Log.e(TAG,"onSpeakBegin");
            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {
                Log.e(TAG,"onBufferProgress");
            }

            @Override
            public void onSpeakPaused() {
                Log.e(TAG,"onSpeakPaused");
                isPause = true;
            }

            @Override
            public void onSpeakResumed() {
                isPause = false;
            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {
            }

            @Override
            public void onCompleted(SpeechError speechError) {
                isComplete = true;
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });

    }

    private void setParams() {
        Log.d(TAG, "Xunfei setParams");
        speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        speechSynthesizer.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
        // 设置在线合成发音人
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //设置合成语速
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");

        //如果不需要保存合成音频，注释该行代码
        speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechSynthesizer.stopSpeaking();
        speechSynthesizer.destroy();
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE,
        };
    }
}
