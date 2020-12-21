package com.cjq.androidx.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

public class CameraSensorListener implements SensorEventListener {

    private static final int DELAY_DURATION = 500;
    private static final int MSG_FOCUS = 1;
    private OnCameraFocusStartListener onCameraFocusStartListener;

    private final SensorManager mSensorManager;
    private final Sensor mSensor;
    private float mX, mY, mZ;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FOCUS:
                    notifyFocus();
                    break;
            }
        }
    };
    private boolean mNeedFocus = true;
    private CameraManager mCameraManager;

    CameraSensorListener(CameraManager cameraManager) {
        this.mCameraManager = cameraManager;
        Context context = cameraManager.getContext();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    void onStart() {
        restParams();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    void onStop() {
        mSensorManager.unregisterListener(this, mSensor);
        mHandler.removeMessages(MSG_FOCUS);
    }

    void setOnCameraFocusStartListener(OnCameraFocusStartListener onCameraFocusStartListener) {
        this.onCameraFocusStartListener = onCameraFocusStartListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mCameraManager.isFocusing()) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float px = Math.abs(x - mX);
            float py = Math.abs(y - mY);
            float pz = Math.abs(z - mZ);
            double value = Math.sqrt(px * px + py * py + pz * pz);

            if (value < 0.8) {
                if (!mHandler.hasMessages(MSG_FOCUS) && mNeedFocus) {
                    mHandler.sendEmptyMessageDelayed(MSG_FOCUS, DELAY_DURATION);
                }
            } else {
                mNeedFocus = true;
                mHandler.removeMessages(MSG_FOCUS);
            }
            mX = x;
            mY = y;
            mZ = z;
        }
    }

    private void notifyFocus() {
        if (onCameraFocusStartListener != null) {
            onCameraFocusStartListener.onCameraFocusStart();
        }
    }

    void onCameraFocused() {
        mNeedFocus = false;
    }

    private void restParams() {
        mX = 0;
        mY = 0;
        mZ = 0;
    }

    public interface OnCameraFocusStartListener {
        void onCameraFocusStart();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
