package com.cjq.androidx.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

/**
 * 图片不能居中显示。因为ImageSpan中只有ImageSpan.ALIGN_BASELINE和ImageSpan.ALIGN_BOTTOM两个选项，即使设置了这个参数，在不同手机上也可能出现不同的情况。
 * 即使同一段代码，也可能出现有的居上，有的居下的情况。解决方法就是自定义ImageSpan，并且重写getSize()和draw()方法
 */
public class MyImageSpan extends ImageSpan {

    public MyImageSpan(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    public MyImageSpan(Context context,  Bitmap bitmap, int verticalAlignment) {
        super(context, bitmap, verticalAlignment);
    }

    public MyImageSpan(@NonNull Drawable drawable) {
        super(drawable);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        try {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        } catch (Exception e) {
            return 20;
        }
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        try {
            Drawable d = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom - top) - d.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            d.draw(canvas);
            canvas.restore();
        } catch (Exception e) {
        }
    }
}
