package com.cjq.androidx.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

import com.cjq.androidx.R;

/**
 *
 */
public class WatermarkContainerView extends RelativeLayout {
    private ViewDragHelper mDragHelper;
    private boolean mDeviceVertical = true;
    private boolean dragEnabled;
    private Rect mChildRect = new Rect();

    public WatermarkContainerView(Context context) {
        super(context);
        init();
    }

    public WatermarkContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WatermarkContainerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setDragEnabled(boolean dragEnabled) {
        this.dragEnabled = dragEnabled;
    }

    public boolean isDragEnabled() {
        return dragEnabled;
    }

    private void init() {
        WaterMarkerDragCallback dragCallback = new WaterMarkerDragCallback();
        mDragHelper = ViewDragHelper.create(this, dragCallback);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragEnabled && mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dragEnabled) {
            mDragHelper.processTouchEvent(event);
        }
        return true;
    }

    public void setWatermarkRotation(int rotation) {
        this.mDeviceVertical = rotation % 180 == 0;
    }

    private int getChildWidth(View child) {
        return (mDeviceVertical ? child.getWidth() : child.getHeight());
    }

    private int getChildHeight(View child) {
        return (mDeviceVertical ? child.getHeight() : child.getWidth());
    }

    public Rect getChildBounds(View child) {
        int left = child.getLeft();
        int top = child.getTop();
        int width = getChildWidth(child);
        int height = getChildHeight(child);
        int dis = Math.abs(width - height) / 2;
        if (mDeviceVertical) {
            mChildRect.left = left;
            mChildRect.top = top;
        } else {
            mChildRect.left = left + dis;
            mChildRect.top = top - dis;
        }
        mChildRect.right = mChildRect.left + width;
        mChildRect.bottom = mChildRect.top + height;
        return mChildRect;
    }

    public void adjustBounds(View child) {
        Rect bounds = getChildBounds(child);
        int dx = 0;
        int dy = 0;
        if (bounds.left < 0) {
            dx = -bounds.left;
        } else if (bounds.right > getWidth()) {
            dx = getWidth() - bounds.right;
        }
        if (bounds.top < 0) {
            dy = -bounds.top;
        } else if (bounds.bottom > getHeight()) {
            dy = getHeight() - bounds.bottom;
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        // remove align_parent_bottom rule
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

        int childLeft = child.getLeft() + dx;
        int childTop = child.getTop() + dy;
        layoutWatermark(child, lp, childLeft, childTop);
    }

    private void layoutWatermark(View child, LayoutParams lp, int childLeft, int childTop) {
        // if margin more than parent width
        if (!mDeviceVertical && childLeft + child.getWidth() > getWidth()) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.leftMargin = 0;
            lp.rightMargin = getWidth() - childLeft - child.getWidth();
            lp.topMargin = childTop;
            lp.bottomMargin = 0;
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.leftMargin = childLeft;
            lp.topMargin = childTop;
            lp.bottomMargin = 0;
            lp.rightMargin = 0;
        }
        child.setLayoutParams(lp);
    }

    private class WaterMarkerDragCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child.getId() == R.id.llWatermark;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return getWidth() - getChildWidth(child);
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return getHeight() - getChildHeight(child);
        }

        @Override
        public void onViewPositionChanged(@NonNull View child, int left, int top, int dx, int dy) {
            if (!dragEnabled) {
                return;
            }
            // ViewDragHelper *not* really change the view layout position, when the view layout again,
            // such as *invalidate()* method called, the drag position of it will be cleared, and the
            // view visual position will be reset.
            // Make the child *really* move to the drag position.
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            // remove align_parent_bottom rule
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            int childLeft = child.getLeft();
            int childTop = child.getTop();
            layoutWatermark(child, lp, childLeft, childTop);
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            int delta = (mDeviceVertical ? 0 : getChildWidth(child) - getChildHeight(child)) / 2;
            if (left < delta) {
                return delta;
            } else {
                int childWidth = getChildWidth(child);
                int width = getWidth();
                if (left + childWidth + Math.abs(delta) > width) {
                    return width - Math.abs(delta) - childWidth;
                } else {
                    return left;
                }
            }
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            int delta = (mDeviceVertical ? 0 : getChildHeight(child) - getChildWidth(child)) / 2;
            if (top < Math.abs(delta)) {
                return Math.abs(delta);
            } else {
                int childHeight = getChildHeight(child);
                int height = getHeight();
                if (top + childHeight > height + Math.abs(delta)) {
                    return height + Math.abs(delta) - childHeight;
                } else {
                    return top;
                }
            }
        }
    }
}

