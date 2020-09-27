package com.cjq.androidx.interfaces;

import android.net.Uri;
import com.annimon.stream.Stream;
import com.blankj.utilcode.util.UriUtils;
import com.cjq.androidx.bean.LocalMedia;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class ImageChooser implements IFileChooser, OnResultCallbackListener<LocalMedia> {

    private ValueCallback<Uri[]> mFilePathCallback;
    private boolean mSelMulti;
    private OnCustomImageSelectListener selectListener;

    public ImageChooser() {
    }

    public ImageChooser(OnCustomImageSelectListener listener) {
        this.selectListener = listener;
    }

    @Override
    public void onSelect(ValueCallback<Uri[]> filePathCallback,
                         WebChromeClient.FileChooserParams fileChooserParams) {
        mFilePathCallback = new WeakReference<>(filePathCallback).get();
        if (fileChooserParams != null) {
            if (fileChooserParams.getMode() == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE) {
                mSelMulti = true;
            } else if (fileChooserParams.getMode() == WebChromeClient.FileChooserParams.MODE_OPEN) {
                mSelMulti = false;
            }
        }
        //ImageSelectActivity.start(mSelMulti, this, selectListener);
    }

    @Override
    public void onResult(List<LocalMedia> result) {
        if (mFilePathCallback != null) {
            Uri[] uris = Stream.of(result).map(localMedia ->
                    UriUtils.file2Uri(new File(localMedia.getCutPath()))).toArray(Uri[]::new);
            mFilePathCallback.onReceiveValue(uris);
        }
    }

    @Override
    public void onCancel() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }
    }
}
