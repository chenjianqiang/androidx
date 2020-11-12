package com.cjq.androidx.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityTtsDemoBinding;
import com.cjq.androidx.tools.MyMediaManager;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;


/**
 * 使用系统自带TTS功能，支持暂停(先合成，生成本地音频文件 )
 */
public class TTSDemoActivity extends BigBaseActivity implements TextToSpeech.OnInitListener{
    private String TAG = "TTSDemoActivity";
    private ActivityTtsDemoBinding mView;
    private TextToSpeech mTts;
    private String wavPath = Environment.getExternalStorageDirectory() + "/tts_temp.wav";
    private HashMap<String, String> myHashRender = new HashMap();
    private MyMediaManager myMediaManager;
    private boolean isPause;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this,R.layout.activity_tts_demo);
        mTts = new TextToSpeech(this,this::onInit);
        myMediaManager = new MyMediaManager(this);
        mView.setOnClickListener(this);
    }

    private void preLoadAudio() {
        isPause = false;
        File file = new File(wavPath);
        if(file.exists()){
            file.delete();
        }
        String ttsMsg = mView.ttsMsg.getText().toString();
        if(StringUtils.isTrimEmpty(ttsMsg)){
            return;
        }
        Log.e(TAG, "synthesizeToFile begin");
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, ttsMsg);
        int r = mTts.synthesizeToFile(ttsMsg, myHashRender, wavPath);
        if (r == TextToSpeech.SUCCESS) {
            Log.e(TAG, "save audio_file success" + wavPath);
        } else {
            Log.e(TAG, "save audio_file fail");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tts_start:
                ToastUtils.showShort("开始播报");
                //tts.speak(ttsMsg,TextToSpeech.QUEUE_ADD,null);
                myMediaManager.playSound(wavPath,mp -> {
                    ToastUtils.showShort("播放完成");
                });
                break;
            case R.id.tts_stop:
                ToastUtils.showShort("停止TTS");
                //stopTTS();
                myMediaManager.stop();
                break;
            case R.id.tts_pause:
                if(!isPause) {
                    isPause = true;
                    mView.ttsPause.setText("继续播报");
                    myMediaManager.pause();
                }else{
                    isPause = false;
                    mView.ttsPause.setText("暂停播报");
                    myMediaManager.resume();
                }
                break;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //设置播放语言
            int result = mTts.setLanguage(Locale.CHINESE);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                ToastUtils.showShort("不支持TTS");
            } else if (result == TextToSpeech.LANG_AVAILABLE) {
                //初始化成功之后才可以播放文字
                //否则会提示“speak failed: not bound to tts engine
                //TextToSpeech.QUEUE_ADD会将加入队列的待播报文字按顺序播放
                //TextToSpeech.QUEUE_FLUSH会替换原有文字
                ToastUtils.showShort("支持TTS");
                preLoadAudio();
            }

        } else {
            Log.e(TAG, "初始化失败");
        }
    }

    public void stopTTS() {
        if (mTts != null) {
            mTts.shutdown();
            mTts.stop();
            mTts = null;
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
                Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }
}
