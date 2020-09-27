package com.cjq.androidx.interfaces;

import android.app.Activity;

public interface OnCustomImageSelectListener {
    /**
     * 照相机
     *
     * @param activity
     * @param requestCode
     */
    void onCamera(Activity activity, int requestCode);

    /**
     * 图库
     *
     * @param activity
     * @param selMulti
     * @param requestCode
     */
    void onGallery(Activity activity, boolean selMulti, int requestCode);
}
