package com.cjq.androidx.media;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * A proxy of {@link MediaPlayer}
 */
class Player implements LifecycleObserver {

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private boolean mIsReleased;

    Player(Context context) {
        this.mContext = context.getApplicationContext();
    }

    void play(String videoPath, SurfaceTexture surfaceTexture, MediaPlayer.OnCompletionListener completionListener) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(videoPath));
        mMediaPlayer.setSurface(new Surface(surfaceTexture));
        mMediaPlayer.setOnCompletionListener(completionListener);
        mMediaPlayer.start();
    }

    void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    void stop() {
        mMediaPlayer.stop();
    }

    void pause() {
        mMediaPlayer.pause();
    }

    void resume() {
        mMediaPlayer.start();
    }

    boolean isPlaying() {
        return mMediaPlayer != null && !mIsReleased && mMediaPlayer.isPlaying();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mIsReleased = true;
        }
    }
}

