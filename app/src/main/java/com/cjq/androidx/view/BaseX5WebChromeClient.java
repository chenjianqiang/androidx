package com.cjq.androidx.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import com.blankj.utilcode.util.Utils;
import com.cjq.androidx.R;
import com.cjq.androidx.interfaces.IFileChooser;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

public class BaseX5WebChromeClient extends WebChromeClient {
    private ObjectAnimator mProgressAnimator;
    private ProgressBar progressBar;
    private int targetProgress;

    public BaseX5WebChromeClient(X5WebView x5WebView) {
        progressBar = x5WebView.findViewById(R.id.progress);
        if (progressBar == null) {
            LayoutInflater inflater = LayoutInflater.from(Utils.getApp());
            inflater.inflate(R.layout.view_progressbar, x5WebView);
            progressBar = x5WebView.findViewById(R.id.progress);
        }
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void onProgressChanged(WebView webView, int i) {
        super.onProgressChanged(webView, i);
        if (progressBar == null) {
            return;
        }
        int progress = progressBar.getProgress();
        boolean progressChanged = targetProgress != i;
        if (mProgressAnimator != null && progressChanged) {
            mProgressAnimator.cancel();
        }
        int dis = i - progress;
        if (dis > 0 && progressChanged) {
            targetProgress = i;
            mProgressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progress, i);
            mProgressAnimator.setInterpolator(new DecelerateInterpolator());
            boolean completed = i == 100;
            if (completed) {
                mProgressAnimator.setDuration(dis * 3);
                mProgressAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                mProgressAnimator.setDuration(dis * 10);
            }
            mProgressAnimator.start();
        }
    }

    /**
     * Tell the client to show a file chooser.
     * <p>
     * This is called to handle HTML forms with 'file' input type, in response to the
     * user pressing the "Select File" button.
     * To cancel the request, call <code>filePathCallback.onReceiveValue(null)</code> and
     * return {@code true}.
     *
     * @param webView           The WebView instance that is initiating the request.
     * @param filePathCallback  Invoke this callback to supply the list of paths to files to upload,
     *                          or {@code null} to cancel. Must only be called if the
     *                          {@link #onShowFileChooser} implementation returns {@code true}.
     * @param fileChooserParams Describes the mode of file chooser to be opened, and options to be
     *                          used with it.
     * @return {@code true} if filePathCallback will be invoked, {@code false} to use default
     * handling.
     * @see android.webkit.WebChromeClient.FileChooserParams
     */
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (webView instanceof X5WebView) {
            IFileChooser fileChooser = ((X5WebView) webView).getFileChooser();
            if (fileChooser != null) {
                fileChooser.onSelect(filePathCallback, fileChooserParams);
                return true;
            }
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    public void onWebViewDestroy() {
        if (mProgressAnimator != null && mProgressAnimator.isStarted()) {
            mProgressAnimator.cancel();
        }
        progressBar = null;
    }
}

