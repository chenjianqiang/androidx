package com.cjq.androidx.interfaces;

import android.net.Uri;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;

public interface IFileChooser {
    /**
     * 选择文件
     *
     * @param filePathCallback
     * @param fileChooserParams
     */
    void onSelect(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);
}
