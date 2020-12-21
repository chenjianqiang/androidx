package com.cjq.androidx.media;

import android.Manifest;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

import com.blankj.utilcode.util.FileUtils;
import java.util.concurrent.atomic.AtomicBoolean;

class Recorder {

    private final Context mContext;
    private MediaRecorder mMediaRecorder;
    private CameraManager mCameraManager;
    private String saveVideoPath;
    private AtomicBoolean isRecording = new AtomicBoolean(false);
    int fileFormat;
    private long mStartRecordTime;
    private long mStopRecordTime;
    private CamcorderProfile profile;
    private boolean isDestroyed;

    Recorder(CameraManager cameraManager) {
        mCameraManager = cameraManager;
        mContext = mCameraManager.getContext();
    }

    void startRecord(String saveVideoPath) {
        long l = SystemClock.uptimeMillis();
        CameraManager.OnErrorListener errorListener = mCameraManager.getErrorListener();
        if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) !=
                PermissionChecker.PERMISSION_GRANTED) {
            if (errorListener != null) {
                errorListener.onError(CameraManager.ERROR_NO_PERMISSION);
            }
            return;
        }
        Camera camera = mCameraManager.getCamera();
        if (camera == null) {
            if (errorListener != null) {
                errorListener.onError(CameraManager.ERROR_NO_CAMERA);
            }
            return;
        }
        TextureView preview = mCameraManager.getPreview();
        if (preview == null) {
            if (errorListener != null) {
                errorListener.onError(CameraManager.ERROR_NO_PREVIEW);
            }
            return;
        }
        if (!preview.isAvailable()) {
            if (errorListener != null) {
                errorListener.onError(CameraManager.ERROR_PREVIEW_DISABLED);
            }
            return;
        }
        if (!mCameraManager.isPreviewing()) {
            if (errorListener != null) {
                errorListener.onError(CameraManager.ERROR_NOT_PREVIEWING);
            }
            return;
        }
        this.saveVideoPath = saveVideoPath;
        CamcorderProfile profile = getCamcorderProfile();
        if (profile == null) {
            if (errorListener != null) {
                errorListener.onError(CameraManager.ERROR_NO_CAMCORDER_PROFILE);
            }
            return;
        }
        fileFormat = profile.fileFormat;
        this.profile = profile;
        camera.unlock();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(profile);
        mMediaRecorder.setOutputFile(saveVideoPath);
        mMediaRecorder.setOrientationHint(mCameraManager.computeCameraRotation());
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mStartRecordTime = SystemClock.uptimeMillis();
            isRecording.set(true);
            mCameraManager.mErrorCode = 0;
            Log.i("test", "startRecord: " + (mStartRecordTime - l));
        } catch (Exception e) {
            e.printStackTrace();
            release(false);
            if (errorListener != null) {
                errorListener.onError(CameraManager.ERROR_CAN_NOT_START_RECORDER);
            }
        }
    }

    CamcorderProfile getProfile() {
        return profile;
    }

    @Nullable
    private CamcorderProfile getCamcorderProfile() {
        CamcorderProfile profile = null;
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_2160P)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_2160P);
            }
        }
        if (profile == null) {
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_CIF)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_CIF);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QCIF)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_QCIF);
            }
        }
        return profile;
    }

    boolean isRecording() {
        return isRecording.get();
    }

    void stopRecord() {
        if (isRecording.get()) {
            long t = SystemClock.uptimeMillis();
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
                FileUtils.delete(saveVideoPath);
                // maybe record time too short
                CameraManager.OnErrorListener errorListener = mCameraManager.getErrorListener();
                if (errorListener != null) {
                    errorListener.onError(CameraManager.ERROR_CAN_NOT_STOP_RECORDER);
                }
            } finally {
                mStopRecordTime = SystemClock.uptimeMillis();
                Log.i("test", "stopRecord: " + (mStopRecordTime - t));
                release(false);
                isRecording.set(false);
            }
        }
    }

    long getRecordTime() {
        return mStopRecordTime - mStartRecordTime;
    }

    void release(boolean destroy) {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
        }
        Camera camera = mCameraManager.getCamera();
        if (camera != null) {
            camera.lock();
        }
        if (destroy) {
            mCameraManager = null;
            isDestroyed = true;
        }
    }

    boolean isDestroyed() {
        return isDestroyed;
    }
}
