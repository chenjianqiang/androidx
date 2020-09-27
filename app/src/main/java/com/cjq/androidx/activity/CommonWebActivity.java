package com.cjq.androidx.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.CommonWebView;
import com.cjq.androidx.interfaces.ImageChooser;
import com.cjq.androidx.interfaces.OnCustomImageSelectListener;
import com.cjq.androidx.view.BaseJsObject;
import com.cjq.androidx.view.WebPageBuilder;
import com.gyf.immersionbar.ImmersionBar;

import java.lang.ref.WeakReference;

public class CommonWebActivity extends AppCompatActivity {
    private static final String JS_DONE_RES = "1";
    private CommonWebView mBinding;
    private WebPageBuilder mWebPageBuilder;
    /**
     * 静态js交互事件容器
     * 静态全局单例，（如果启动多个CommonWebActivity单例够用？答案是没有任何影响）
     * 因为只用于webview addJavascriptInterface方法，这里只要该方法被调用后，对象被立即回收，不影响js交互。
     */
    private static BaseJsObject mJsObject;

    /**
     * 自定义图片选择器
     */
    private static OnCustomImageSelectListener mOnCustomImageSelectListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebPageBuilder = getIntent().getParcelableExtra("webConfig");
        if (mWebPageBuilder == null) {
            mWebPageBuilder = WebPageBuilder.defaultBuilder();
        }
        // 沉浸式
        if (ImmersionBar.isSupportStatusBarDarkFont()) {
            ImmersionBar.with(this)
                    .supportActionBar(false)
                    .fitsSystemWindows(true)
                    .statusBarDarkFont(true)
                    .statusBarColorInt(mWebPageBuilder.colorAccent == 0 ? Color.WHITE : mWebPageBuilder.colorAccent)
                    .init();
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_common_web);
        initView();
    }

    private void initView() {
        mBinding.setColorAccent(mWebPageBuilder.colorAccent);
        mBinding.setColorAccentTint(mWebPageBuilder.colorAccentTint);
        mBinding.setTitle(mWebPageBuilder.title);
        mBinding.tvTitle.setTypeface(null, mWebPageBuilder.titleIsBold ? Typeface.BOLD : Typeface.NORMAL);
        mBinding.btnBack.setOnClickListener(v -> this.onBackPressed());
        if (mWebPageBuilder.enableChooseImg) {
            mBinding.webView.setFileChooser(mWebPageBuilder.isCustomImageSelect() ? new ImageChooser(mOnCustomImageSelectListener) : new ImageChooser());
        }
        // js交互事件
        if (mJsObject != null) {
            mBinding.webView.addJavascriptInterface(mJsObject.setX5WebView(mBinding.webView));
            mJsObject = null;
        } else {
            mBinding.webView.addJavascriptInterface(new BaseJsObject().setX5WebView(mBinding.webView));
        }
        if (TextUtils.isEmpty(mWebPageBuilder.url)) {
            ToastUtils.showShort("页面地址为空");
            finish();
            return;
        }
        mBinding.webView.loadUrl(mWebPageBuilder.url, mWebPageBuilder.params);
    }

    public String getUmPageCode() {
        return mWebPageBuilder.getPageCode();
    }

    @Override
    public void onBackPressed() {
        if (mWebPageBuilder.enableBackJsJoin) {
            // 前端js响应返回事件
            onJsBackPress();
        } else {
            super.onBackPressed();
        }
    }

    private void onJsBackPress() {
        mBinding.webView.evaluateJavascript("javascript:onBackPressed()", s -> {
            LogUtils.d("javascript:onBackPressed() callback = " + s);
            if (!JS_DONE_RES.equals(s)) {
                super.onBackPressed();
            }
        });
    }

    public static void start(WebPageBuilder builder) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("webConfig", builder);
        ActivityUtils.startActivity(bundle, CommonWebActivity.class);
    }

    public static <T extends BaseJsObject> void setJsObject(@NonNull T object) {
        mJsObject = new WeakReference<>(object).get();
    }

    public static void setOnCustomImageSelectListener(OnCustomImageSelectListener listener) {
        mOnCustomImageSelectListener = new WeakReference<>(listener).get();
    }
}
