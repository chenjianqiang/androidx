package com.cjq.androidx.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.ColorInt;
import com.cjq.androidx.activity.CommonWebActivity;
import com.cjq.androidx.interfaces.OnCustomImageSelectListener;

public class WebPageBuilder implements Parcelable {
    /**
     * colorAccent
     */
    @ColorInt
    public int colorAccent = Color.WHITE;

    /**
     * 在colorAccent背景色上的文本或者按钮着色
     */
    @ColorInt
    public int colorAccentTint = Color.BLACK;

    /**
     * 标题
     */
    public String title;

    /**
     * 页面加载链接
     */
    public String url;

    /**
     * 标题是否加粗
     */
    public boolean titleIsBold;

    /**
     * 是否可以选择照片
     */
    public boolean enableChooseImg;

    /**
     * 返回是否要js响应处理
     */
    public boolean enableBackJsJoin;

    /**
     * 页面加载携带参数
     */
    public Bundle params;

    /**
     * h5 js交互事件
     */
    public BaseJsObject jsObject;

    /**
     * 是否自定义图片选择
     */
    private boolean isCustomImageSelect;

    /**
     * 图片选择监听
     */
    private OnCustomImageSelectListener onCustomImageSelectListener;
    /**
     * 友盟埋点的PageCode
     */
    private String pageCode;

    public WebPageBuilder() {
    }

    protected WebPageBuilder(Parcel in) {
        colorAccent = in.readInt();
        colorAccentTint = in.readInt();
        title = in.readString();
        url = in.readString();
        titleIsBold = in.readByte() != 0;
        enableChooseImg = in.readByte() != 0;
        enableBackJsJoin = in.readByte() != 0;
        params = in.readBundle();
        isCustomImageSelect = in.readByte() != 0;
        pageCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(colorAccent);
        dest.writeInt(colorAccentTint);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeByte((byte) (titleIsBold ? 1 : 0));
        dest.writeByte((byte) (enableChooseImg ? 1 : 0));
        dest.writeByte((byte) (enableBackJsJoin ? 1 : 0));
        dest.writeBundle(params);
        dest.writeByte((byte) (isCustomImageSelect ? 1 : 0));
        dest.writeString(pageCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WebPageBuilder> CREATOR = new Creator<WebPageBuilder>() {
        @Override
        public WebPageBuilder createFromParcel(Parcel in) {
            return new WebPageBuilder(in);
        }

        @Override
        public WebPageBuilder[] newArray(int size) {
            return new WebPageBuilder[size];
        }
    };

    public WebPageBuilder setColorAccent(int colorAccent) {
        this.colorAccent = colorAccent;
        return this;
    }

    public WebPageBuilder setColorAccentTint(int colorAccentTint) {
        this.colorAccentTint = colorAccentTint;
        return this;
    }

    public WebPageBuilder setEnableChooseImg(boolean enableChooseImg) {
        this.enableChooseImg = enableChooseImg;
        return this;
    }

    public WebPageBuilder setEnableBackJsJoin(boolean enableBackJsJoin) {
        this.enableBackJsJoin = enableBackJsJoin;
        return this;
    }

    public WebPageBuilder setParams(Bundle params) {
        this.params = params;
        return this;
    }

    public <T extends BaseJsObject> WebPageBuilder setJsObject(T jsObject) {
        this.jsObject = jsObject;
        return this;
    }

    public WebPageBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public WebPageBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public WebPageBuilder setTitleIsBold(boolean titleIsBold) {
        this.titleIsBold = titleIsBold;
        return this;
    }

    public WebPageBuilder setParam(String key, String value) {
        if (this.params == null) {
            this.params = new Bundle();
        }
        this.params.putString(key, value);
        return this;
    }


    public WebPageBuilder setOnCustomImageSelectListener(
            OnCustomImageSelectListener selectListener) {
        this.onCustomImageSelectListener = selectListener;
        this.isCustomImageSelect = true;
        return this;
    }

    public WebPageBuilder setUmPageCode(
            String pageCode) {
        this.pageCode = pageCode;
        return this;
    }

    public String getPageCode() {
        return pageCode;
    }

    public boolean isCustomImageSelect() {
        return isCustomImageSelect;
    }

    public static WebPageBuilder defaultBuilder() {
        WebPageBuilder builder = new WebPageBuilder();
        return builder;
    }

    public void buildPage() {
        if (jsObject != null) {
            CommonWebActivity.setJsObject(jsObject);
        }
        CommonWebActivity.setOnCustomImageSelectListener(onCustomImageSelectListener);
        CommonWebActivity.start(this);
    }

}