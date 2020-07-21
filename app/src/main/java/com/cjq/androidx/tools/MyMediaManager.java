package com.cjq.androidx.tools;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.IOException;

/**
 * @author cjq
 * 录音播放类
 */
public class MyMediaManager implements LifecycleObserver {
    private MediaPlayer mMediaPlayer;

    private boolean isPause;

    private static MyMediaManager instance;

    public static MyMediaManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MyMediaManager.class) {
                if (instance == null) {
                    instance = new MyMediaManager(context);
                }
            }
        }
        return instance;
    }

    public MyMediaManager(Context context) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //播放错误 防止崩溃
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
        }
    }

    //播放录音
    public void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            File file = new File(filePath);
            if(file.exists()){
                mMediaPlayer.setDataSource(filePath);
            }else {
                mMediaPlayer.setDataSource(Utils.getApp(), Uri.parse(filePath));
            }
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }

    /**
     * 如果 播放时间过长,如30秒
     * 用户突然来电话了,则需要暂停
     */
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 播放
     */
    public void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * activity 被销毁  释放
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            instance = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {
        Log.e("TTSDemoActivity", "MyMediaManager onPause()");
        pause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        Log.e("TTSDemoActivity", " MyMediaManager onResume()");
        resume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.e("TTSDemoActivity", " MyMediaManager release()");
        release();
    }
}
