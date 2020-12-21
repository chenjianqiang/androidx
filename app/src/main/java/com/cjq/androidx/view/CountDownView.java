package com.cjq.androidx.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import androidx.annotation.IntRange;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.blankj.utilcode.util.SizeUtils;

public class CountDownView extends androidx.appcompat.widget.AppCompatTextView {
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_END = 2;
    public static final int STATUS_CANCELED = 3;

    private Paint mPaint;
    private int ringColor = 0xFFE7E7E7;
    private int progressColor = 0xFF69BA59;
    private float ringWidth = SizeUtils.dp2px(2);
    private int holeColor = 0;
    private RectF mRingRect = new RectF();
    private float mSweepAngel;
    private int count;
    private long mStartTime;
    private MutableLiveData<Integer> countTick = new MutableLiveData<>();
    private MutableLiveData<Integer> countStatus = new MutableLiveData<>();

    public CountDownView(Context context) {
        super(context);
        init();
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
    }

    public void setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
    }

    public void setHoleColor(int holeColor) {
        this.holeColor = holeColor;
    }

    public void startCount(@IntRange(from = 1) int count) {
        this.count = count;
        mSweepAngel = 0;
        mStartTime = SystemClock.uptimeMillis();
        invalidate();
        countTick.setValue(count);
        countStatus.setValue(STATUS_STARTED);
    }

    public void cancel() {
        Integer currentStatus = countStatus.getValue();
        if (currentStatus != null && currentStatus == STATUS_STARTED) {
            countStatus.setValue(STATUS_CANCELED);
        }
    }

    public LiveData<Integer> getCountTick() {
        return countTick;
    }

    public MutableLiveData<Integer> getCountStatus() {
        return countStatus;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float offset = ringWidth / 2;
        mRingRect.set(offset, offset, getWidth() - offset, getHeight() - offset);
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = mRingRect.width();
        float h = mRingRect.height();
        int r = (int) (Math.min(w, h) / 2);
        mPaint.setColor(holeColor);
        mPaint.setStyle(Paint.Style.FILL);
        float cx = mRingRect.centerX();
        float cy = mRingRect.centerY();
        canvas.drawCircle(cx, cy, r - this.ringWidth / 2, mPaint);
        mPaint.setColor(ringColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(this.ringWidth);
        canvas.drawCircle(cx, cy, r, mPaint);

        Integer currentStatus = countStatus.getValue();
        if (currentStatus != null && currentStatus != STATUS_CANCELED && currentStatus != STATUS_END) {
            long currentTime = SystemClock.uptimeMillis();
            long dis = currentTime - mStartTime;
            long elapsedSec = dis / 1000;
            int currentCount = (int) (count - elapsedSec);
            Integer previousCount = countTick.getValue();
            if (previousCount != null && currentCount != previousCount) {
                countTick.setValue(currentCount);
            }
            if (dis < count * 1000) {
                mSweepAngel = dis * 0.361f / count;
                mPaint.setColor(progressColor);
                canvas.drawArc(mRingRect, -90, mSweepAngel, false, mPaint);
                postInvalidateDelayed(10);
            } else {
                mSweepAngel = 0;
                countStatus.setValue(STATUS_END);
            }
        }
        super.onDraw(canvas);
    }
}