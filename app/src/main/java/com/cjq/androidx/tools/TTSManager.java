package com.cjq.androidx.tools;

import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

public class TTSManager implements InitListener, SynthesizerListener {
    private static final String TAG = "TTSManager";
    private static volatile TTSManager instance = null;
    // 默认发音人
    private String voicer = "xiaoyan";
    private boolean isInitSuccess = false;
    private SpeechSynthesizer mTts;

    public TTSManager() {
        mTts = SpeechSynthesizer.createSynthesizer(Utils.getApp(), this);
    }

    public static TTSManager getInstance() {
        if (instance == null) {
            synchronized (TTSManager.class) {
                if (instance == null) {
                    instance = new TTSManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void onInit(int i) {
        Log.d(TAG, "Xunfei InitListener init() code = " + i);
        if (i != ErrorCode.SUCCESS) {
            ToastUtils.showShort("初始化失败：" + i);
        } else {
            // 初始化成功，之后可以调用startSpeaking方法
            // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
            // 正确的做法是将onCreate中的startSpeaking调用移至这里
            isInitSuccess = true;
            setTTSParams();
        }
    }

    public void pause() {
        if (null != mTts) {
            mTts.pauseSpeaking();
        }
    }

    public void resume() {
        if (null != mTts) {
            mTts.resumeSpeaking();
        }
    }

    public void stop() {
        if (null != mTts) {
            mTts.stopSpeaking();
        }
    }

    public void release() {
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    private void setTTSParams() {
        Log.d(TAG, "Xunfei setParams");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");

        //如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
    }

    @Override
    public void onSpeakBegin() {
        // 开始播放
    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {
// 合成进度
    }

    @Override
    public void onSpeakPaused() {
// 暂停播放
    }

    @Override
    public void onSpeakResumed() {
// 继续播放
    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {
// 播放进度
    }

    @Override
    public void onCompleted(SpeechError speechError) {
        if (speechError != null) {
            Log.d(TAG, "onCompleted: " + speechError.getPlainDescription(true));
        }
    }

    @Override
    public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
        //以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            String sid = bundle.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            Log.d(TAG, "session id =" + sid);
        }
    }
}
