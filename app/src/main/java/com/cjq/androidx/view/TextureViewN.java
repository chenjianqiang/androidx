package com.cjq.androidx.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

public class TextureViewN extends TextureView {
    public TextureViewN(Context context) {
        super(context);
    }

    public TextureViewN(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextureViewN(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            super.setBackgroundDrawable(background);
        }
    }
}

