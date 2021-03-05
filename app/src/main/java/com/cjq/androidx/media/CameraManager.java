package com.cjq.androidx.media;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A camera manager used to control camera.<br/>
 * <p>
 * Created on 2018/8/20 12:40
 *
 */
@SuppressWarnings({"UNUSED", "DEPRECATION"})
public class CameraManager
        implements LifecycleObserver,
        TextureView.SurfaceTextureListener,
        CameraSensorListener.OnCameraFocusStartListener {
    /**
     * The {@link #mPreview} is null.
     */
    public static final int ERROR_NO_PREVIEW = -1;
    /**
     * Permission {@link Manifest.permission#CAMERA} not granted.
     */
    public static final int ERROR_NO_PERMISSION = -2;
    /**
     * Camera is using in other process.
     */
    public static final int ERROR_CAMERA_BUSY = -3;
    /**
     * The {@link #mPreview} is disabled, maybe destroyed.
     */
    public static final int ERROR_PREVIEW_DISABLED = -4;
    /**
     * The {@link #mPreview} is not created.
     */
    public static final int ERROR_PREVIEW_NOT_CREATED = -5;
    /**
     * Device has no camera hardware.
     */
    public static final int ERROR_DEVICE_NO_CAMERA = -6;
    /**
     * Device has no camera match the specified facing.
     */
    public static final int ERROR_DEVICE_NO_FACING_CAMERA = -7;
    /**
     * The camera is destroyed.
     */
    public static final int ERROR_NO_CAMERA = -8;
    /**
     * Not previewing now.
     */
    public static final int ERROR_NOT_PREVIEWING = -9;
    /**
     * Some error happened when start MediaRecorder
     */
    public static final int ERROR_CAN_NOT_START_RECORDER = -10;
    /**
     * Some error happened when stop MediaRecorder, maybe record time too short.
     */
    public static final int ERROR_CAN_NOT_STOP_RECORDER = -11;
    /**
     * No camcorder profile, can not record video.
     */
    public static final int ERROR_NO_CAMCORDER_PROFILE = -12;
    /**
     * Path is invalid
     */
    public static final int ERROR_INVALID_PATH = -13;
    /**
     * Save media file failure.
     */
    public static final int ERROR_CAN_NOT_SAVE_MEDIA_FILE = -14;

    public static final int ERROR_WAIT_SURFACE_AVAILABLE = -15;

    private static final int CHECK_CLICK_TIME = 300;
    private static final int FOCUS_RECT_SIZE = SizeUtils.dp2px(75);
    private static final long FOCUS_INTERVAL = 3000;

    /**
     * Whether is taking picture now.
     */
    private AtomicBoolean mIsTakingPicture = new AtomicBoolean(false);
    /**
     * Whether is previewing now.
     */
    private AtomicBoolean mIsPreviewing = new AtomicBoolean(false);
    /**
     * Used to detect click preview to focus.
     */
    private OnPreviewTouchListener mOnPreviewTouchListener = new OnPreviewTouchListener();
    /**
     * Use a deprecation API {@link Camera} to to compat user's phone with a lower version.
     * The new API {@link android.hardware.camera2.CameraManager} only used when <code>API version >= 21</code>.
     */
    private Camera mCamera;

    private final ScaleGestureDetector mGestureDetector;
    /**
     * Used to auto focus when user moved the device
     */
    private final CameraSensorListener mCameraSensorListener;
    /**
     * Used to determine camera rotation.
     */
    private final OrientationListener mOrientationListener;
    private SurfaceTexture mSurfaceTexture;
    private static CameraManager mInstance;
    private Context mContext;
    private TextureView mPreview;
    private ErrorListenerWrapper mOnErrorListener;
    private OnFocusListener onFocusListener;
    private OnPreviewListener onPreviewListener;

    private boolean exactlyTakePicture = true;
    private boolean mZoomSupported;
    private boolean clickToFocus = true;
    private boolean scaleZoomEnabled = false;
    private boolean autoFocusBySensor = true;
    private boolean mIsFocusing;
    private long mFocusTime;
    private boolean mNeedResumePreview = true;
    private Point previewSize;
    private boolean mWaitForSurfaceAvailable = true;
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
    private final int mTouchSlop;
    private int mWidth;
    private int mHeight;
    private int mCameraId;
    int mErrorCode;
    private boolean recordingHint;

    private CameraManager(Context context) {
        // use application context to prevent memory leak.
        mContext = context.getApplicationContext();
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mGestureDetector = new ScaleGestureDetector(mContext, new OnScaleListener());
        mCameraSensorListener = new CameraSensorListener(this);
        mCameraSensorListener.setOnCameraFocusStartListener(this);
        mOrientationListener = new OrientationListener(mContext);
        mOnErrorListener = new ErrorListenerWrapper();
    }

    public static CameraManager getInstance(@NonNull Context context) {
        synchronized (CameraManager.class) {
            if (mInstance == null) {
                synchronized (CameraManager.class) {
                    mInstance = new CameraManager(context);
                }
            }
        }
        return mInstance;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setPreview(@NonNull TextureView preview) {
        this.mPreview = preview;
        if (clickToFocus) {
            mPreview.setOnTouchListener(mOnPreviewTouchListener);
        }
        mPreview.setSurfaceTextureListener(this);
    }

    public Context getContext() {
        return mContext;
    }

    boolean isAutoFocusBySensor() {
        return autoFocusBySensor;
    }

    boolean isFocusing() {
        return mIsFocusing;
    }

    public Point getPreviewSize() {
        return previewSize;
    }

    public int getDeviceRotation() {
        if (mOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return 0;
        }
        return ((mOrientation + 45) / 90) % 4;
    }

    private class OrientationListener extends OrientationEventListener {

        OrientationListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation != OrientationListener.ORIENTATION_UNKNOWN) {
                mOrientation = orientation;
            }
        }
    }

    /**
     * If set to true, the {@link #takePicture(OnPictureTakenListener)} method will use camera to
     * take a picture. The default value is true, it means that {@link #takePicture(OnPictureTakenListener)}
     * method will really use camera to take picture rather than directly get a bitmap from surface,
     * that will be much fast than use camera, but the result picture will not as well as it.
     */
    public void setExactlyTakePicture(boolean exactlyTakePicture) {
        this.exactlyTakePicture = exactlyTakePicture;
    }

    /**
     * Check whether the camera is usable now and open it if usable.
     *
     * @return a code represent the disable reason, one of the
     * [{@link #ERROR_DEVICE_NO_CAMERA},
     * {@link #ERROR_NO_PERMISSION},
     * 0],
     * 0 means no error.
     */
    public int checkCameraUsable(int cameraFacing, boolean openCamera) {
        int errorCode = 0;
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            errorCode = ERROR_DEVICE_NO_CAMERA;
        } else if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                != PermissionChecker.PERMISSION_GRANTED) {
            errorCode = ERROR_NO_PERMISSION;
        } else if (mCamera == null || mCameraFacing != cameraFacing) {
            releaseCamera();
            int cameraId = getFacingCameraId(cameraFacing);
            if (cameraId < 0) {
                errorCode = ERROR_DEVICE_NO_FACING_CAMERA;
            } else {
                if (openCamera) {
                    try {
                        mCamera = Camera.open(cameraId);
                    } catch (Exception e) {
                        errorCode = ERROR_CAMERA_BUSY;
                    }
                    if (errorCode == 0 && mCamera == null) {
                        errorCode = ERROR_NO_PERMISSION;
                    }
                }
            }
        }
        if (errorCode != 0) {
            releaseCamera();
        }
        return errorCode;
    }

    /**
     * Start camera preview and return an error code if certain error occurred,
     * if no error occurred, return 0.
     *
     * @param cameraFacing camera to open.
     * @see #ERROR_NO_PREVIEW
     * @see #ERROR_NO_PERMISSION
     * @see #ERROR_CAMERA_BUSY
     * @see #ERROR_PREVIEW_NOT_CREATED
     * @see #ERROR_PREVIEW_DISABLED
     * @see #ERROR_DEVICE_NO_CAMERA
     * @see #ERROR_DEVICE_NO_FACING_CAMERA
     */
    public int startPreview(@CameraFacing int cameraFacing) {
        if (isPreviewing() && mCameraFacing == cameraFacing) {
            return 0;
        }
        int errorCode = checkCameraUsable(cameraFacing, true);
        if (errorCode != 0) {
            if (mOnErrorListener != null) {
                mOnErrorListener.onError(errorCode);
            }
            return errorCode;
        }
        int cameraId = getFacingCameraId(cameraFacing);
        boolean hasFacingCamera = cameraId >= 0;
        if (!hasFacingCamera) {
            errorCode = ERROR_DEVICE_NO_FACING_CAMERA;
            if (mOnErrorListener != null) {
                mOnErrorListener.onError(errorCode);
            }
            return errorCode;
        }
        mCameraId = cameraId;
        if (mPreview == null) {
            errorCode = ERROR_NO_PREVIEW;
            if (mOnErrorListener != null) {
                mOnErrorListener.onError(errorCode);
            }
            return errorCode;
        }

        if (mSurfaceTexture == null) {
            // wait for surface texture available
            mWaitForSurfaceAvailable = true;
            errorCode = ERROR_WAIT_SURFACE_AVAILABLE;
            return errorCode;
        }
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
            errorCode = ERROR_PREVIEW_DISABLED;
            if (mOnErrorListener != null) {
                mOnErrorListener.onError(errorCode);
            }
            return errorCode;
        }

        Camera.Parameters parameters = mCamera.getParameters();
        previewSize = getPreferSize(false);
        parameters.setPreviewSize(previewSize.x, previewSize.y);
        Point pictureSize = getPreferSize(true);
        parameters.setPictureSize(pictureSize.x, pictureSize.y);
        int degrees = 0;
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        int windowRotation = windowManager.getDefaultDisplay().getRotation();
        switch (windowRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int cameraDegrees;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraDegrees = (info.orientation + degrees) % 360;
            // compensate the mirror
            cameraDegrees = (360 - cameraDegrees) % 360;
            // back-facing
        } else {
            cameraDegrees = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(cameraDegrees);
        mPreview.requestLayout();
        mZoomSupported = parameters.isZoomSupported();
        parameters.setRecordingHint(recordingHint);
        mCamera.setParameters(parameters);
        mCameraFacing = cameraFacing;
        startPreview();
        mErrorCode = 0;
        return errorCode;
    }

    public void setRecordingHint(boolean recordingHint) {
        this.recordingHint = recordingHint;
    }

    private int getFacingCameraId(@CameraFacing int cameraFacing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraFacing) {
                return i;
            }
        }
        return -1;
    }

    public void setOnPreviewListener(OnPreviewListener onPreviewListener) {
        this.onPreviewListener = onPreviewListener;
    }

    /**
     * Whether user can click preview to change camera focus.
     *
     * @param clickToFocus true is enable
     */
    public void setClickToFocus(boolean clickToFocus) {
        this.clickToFocus = clickToFocus;
    }

    public void setAutoFocusBySensor(boolean autoFocusBySensor) {
        this.autoFocusBySensor = autoFocusBySensor;
    }

    /**
     * Toggle camera facing.
     */
    public void toggleCameraFacing() {
        int facing = mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT
                ? Camera.CameraInfo.CAMERA_FACING_BACK
                : Camera.CameraInfo.CAMERA_FACING_FRONT;
        startPreview(facing);
    }

    /**
     * Get current camera facing.
     *
     * @return the current camera facing
     */
    public int getCameraFacing() {
        return mCameraFacing;
    }

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        this.onFocusListener = onFocusListener;
    }

    public void setScaleZoomEnabled(boolean scaleZoomEnabled) {
        this.scaleZoomEnabled = scaleZoomEnabled;
    }

    private void startPreview() {
        if (mCamera != null && mPreview != null && !isPreviewing()) {
            mPreview.requestLayout();
            mCamera.startPreview();
            mIsPreviewing.set(true);
            if (onPreviewListener != null) {
                onPreviewListener.onPreviewStarted();
            }
        }
    }

    public interface OnPreviewListener {
        void onPreviewStarted();

        void onPreviewStopped();
    }

    private Point getPreferSize(boolean isPicture) {
        Camera.Parameters parameters = mCamera.getParameters();
        int previewWidth = mPreview.getWidth();
        int previewHeight = mPreview.getHeight();
        List<Camera.Size> rawSupportedSizes = isPicture ? parameters.getSupportedPictureSizes()
                : parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no preview size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                Integer aPixels = a.height * a.width;
                Integer bPixels = b.height * b.width;
                return bPixels.compareTo(aPixels);
            }
        });

        double previewAspectRatio;
        if (previewWidth > previewHeight) {
            previewAspectRatio = previewWidth / (double) previewHeight;
        } else {
            previewAspectRatio = previewHeight / (double) previewWidth;
        }

        // remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            // delete if less than minimum size
            if (realWidth * realHeight < 480 * 320) {
                it.remove();
                continue;
            }

            // camera preview width > height
            boolean isCandidatePortrait = realWidth < realHeight;
            // width less than height
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            // ratio for camera
            double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;
            // return absolute value
            double distortion = Math.abs(aspectRatio - previewAspectRatio);
            if (distortion > 0.15) {
                it.remove();
                continue;
            }
            // screen size equal to camera supportedPreviewSize
            if (maybeFlippedWidth == previewWidth && maybeFlippedHeight == previewHeight) {
                return new Point(realWidth, realHeight);
            }
        }

        if (!supportedPreviewSizes.isEmpty()) {
            // default return first supportedPreviewSize,mean largest
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            return new Point(largestPreview.width, largestPreview.height);
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        return new Point(defaultPreview.width, defaultPreview.height);
    }

    /**
     * Stop preview and release camera.
     */
    public void releaseCamera() {
        stopPreview();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Use previous camera facing to start preview
     */
    public void restartPreview() {
        startPreview(getCameraFacing());
    }

    /**
     * Stop camera preview.
     */
    public void stopPreview() {
        if (isPreviewing()) {
            mCamera.stopPreview();
            if (onPreviewListener != null) {
                onPreviewListener.onPreviewStopped();
            }
        }
        mIsPreviewing.set(false);
    }

    public void setOnErrorListener(OnErrorListener listener) {
        this.mOnErrorListener.wrap(listener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        // camera is device shared resource, must release the camera for use by other applications.
        // Note, *DO NOT MOVE RELEASE CAMERA CODE TO ONSTOP*. Above android 7.0, user may open
        // picture-in-picture mode, in that case, if user going to communicate with other app,
        // the camera's host activity will pause but maybe not stop.
        if (isPreviewing()) {
            mNeedResumePreview = true;
        }
        releaseCamera();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (mNeedResumePreview) {
            mNeedResumePreview = false;
            startPreview(mCameraFacing);
        }
        //  do nothing, surface destroyed, and will recreate soon
    }

    public boolean isPreviewNotNull() {
        return mPreview != null;
    }

    public void setZoom(int zoom) {
        if (isPreviewing()) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.isZoomSupported()) {
                parameters.setZoom(zoom);
                mCamera.setParameters(parameters);
            }
        }
    }

    private void updateZoom(int delta) {
        if (isPreviewing()) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.isZoomSupported()) {
                int zoom = parameters.getZoom();
                zoom = zoom + delta;
                int maxZoom = getMaxZoom() - 1;
                if (maxZoom < 0) {
                    return;
                }
                if (zoom > maxZoom) {
                    zoom = maxZoom;
                }
                if (zoom < 0) {
                    zoom = 0;
                }
                parameters.setZoom(zoom);
                mCamera.setParameters(parameters);
            }
        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    public TextureView getPreview() {
        return mPreview;
    }

    private int getMaxZoom() {
        if (isPreviewing()) {
            return mCamera.getParameters().getMaxZoom();
        }
        return 0;
    }

    public boolean isPreviewing() {
        return mCamera != null && mPreview != null && mIsPreviewing.get() && mPreview.isAvailable();
    }

    /**
     * Take picture. If no camera is using now, return null.
     *
     * @param listener a callback with the picture.
     */
    public void takePicture(final OnPictureTakenListener listener) {
        if (mIsTakingPicture.get()) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        int rotation = computeCameraRotation();
        parameters.setRotation(rotation);
        mCamera.setParameters(parameters);
        if (isPreviewing()) {
            // fake take picture, use the surface's bitmap.
            final Bitmap bitmap = mPreview.getBitmap();
            String picturePath = new File(mContext.getExternalCacheDir(), System.currentTimeMillis() + ".jpg").getAbsolutePath();
            if (!exactlyTakePicture &&
                    bitmap != null &&
                    bitmap.getWidth() > 0 &&
                    bitmap.getHeight() > 0) {
                // the fake picture is available
                if (listener != null) {
                    mIsTakingPicture.set(false);
                    ImageUtils.save(bitmap, picturePath, Bitmap.CompressFormat.JPEG);
                    listener.onPictureTaken(picturePath);
                }
                stopPreview();
            } else {
                // really take picture
                mIsTakingPicture.set(true);
                // take picture will automatically stop preview
                mCamera.takePicture(
                        null,
                        null,
                        (data, camera) -> {
                            mIsPreviewing.set(false);
                            mIsTakingPicture.set(false);
                            try {
                                FileIOUtils.writeFileFromBytesByStream(picturePath, data);
                                ExifInterface exifInterface = new ExifInterface(picturePath);
                                if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                    exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_FLIP_HORIZONTAL + "");
                                }
                                exifInterface.saveAttributes();
                                if (listener != null) {
                                    listener.onPictureTaken(picturePath);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                ToastUtils.showShort("拍照失败");
                                restartPreview();
                            }
                        });
            }
        } else {
            ToastUtils.showShort("拍照失败！");
        }
    }

    int computeCameraRotation() {
        // FF007F
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        if (mOrientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
            mOrientation = (mOrientation + 45) / 90 * 90;
            int rotation;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - mOrientation + 360) % 360;
            } else {
                // back-facing camera
                rotation = (info.orientation + mOrientation) % 360;
            }
            return rotation;
        }
        return info.orientation;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // Now that the size is known, set up the camera parameters and begin the preview.
        mSurfaceTexture = surface;
        if (width != mWidth || height != mHeight) {
            // size changed
            mWidth = width;
            mHeight = height;
            if (mWaitForSurfaceAvailable) {
                mWaitForSurfaceAvailable = false;
                startPreview(mCameraFacing);
            }
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        releaseCamera();
        mSurfaceTexture = null;
        mWidth = 0;
        mHeight = 0;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void setCameraFacing(int cameraFacing) {
        this.mCameraFacing = cameraFacing;
    }

    @Override
    public void onCameraFocusStart() {
        if (autoFocusBySensor) {
            focus(mWidth / 2, mHeight / 2);
        }
    }

    OnErrorListener getErrorListener() {
        return mOnErrorListener;
    }

    private class OnScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        private static final float BASE_SCALE_FACTOR = 0.015F;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                return false;
            }
            if (isPreviewing()) {
                int maxZoom = getMaxZoom();
                float zoomStep = maxZoom / 100f;
                float dis = scaleFactor - 1;
                double i = dis / BASE_SCALE_FACTOR;
                int a = (int) Math.ceil(Math.abs(zoomStep * i));
                int delta = dis > 0 ? a : -a;
                updateZoom(delta);
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }

    private class OnPreviewTouchListener implements View.OnTouchListener {
        float downX;
        float downY;
        int downPointerId;
        boolean isClickEvent;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (scaleZoomEnabled && mZoomSupported) {
                mGestureDetector.onTouchEvent(event);
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    isClickEvent = true;
                    downX = event.getX();
                    downY = event.getY();
                    downPointerId = event.getPointerId(event.getActionIndex());
                    break;
                case MotionEvent.ACTION_MOVE:
                    int movePointerId = event.getPointerId(event.getActionIndex());
                    if (downPointerId == movePointerId) {
                        float moveX = event.getX();
                        float moveY = event.getY();
                        if (Math.abs(moveX - downX) > mTouchSlop ||
                                Math.abs(moveY - downY) > mTouchSlop) {
                            // down pointer moved
                            isClickEvent = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isClickEvent && event.getEventTime() - event.getDownTime() <= CHECK_CLICK_TIME) {
                        // if down finger no move or move distance less than mTouchSlop, and down a short time, we
                        // can say it is a click event.
                        focus(((int) downX), ((int) downY));
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    /**
     * Let camera focus the specified position
     *
     * @param x x position in preview
     * @param y y position in preview
     */
    public void focus(int x, int y) {
        if (!isPreviewing()) {
            return;
        }
        long currentTime = SystemClock.uptimeMillis();
        if (currentTime - mFocusTime > FOCUS_INTERVAL) {
            mIsFocusing = false;
        }
        if (mIsFocusing) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Rect rect = new Rect(x - FOCUS_RECT_SIZE, y - FOCUS_RECT_SIZE, x + FOCUS_RECT_SIZE, y + FOCUS_RECT_SIZE);
        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> areas = new ArrayList<>();
            // map screen rect to camera focus rect, camera focus rect is [-1000,-1000] to [1000,1000]
            int left = rect.left * 2000 / mPreview.getWidth() - 1000;
            int top = rect.top * 2000 / mPreview.getHeight() - 1000;
            int right = rect.right * 2000 / mPreview.getWidth() - 1000;
            int bottom = rect.bottom * 2000 / mPreview.getHeight() - 1000;
            // limit rect bounds to prevent error
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            // set mapped focus rect to camera, note, only a previewing camera can set focus.
            areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
            parameters.setFocusAreas(areas);
            mCamera.setParameters(parameters);
        }

        if (onFocusListener != null) {
            onFocusListener.onFocusStart(rect);
        }
        mFocusTime = SystemClock.uptimeMillis();
        mIsFocusing = true;
        mCamera.autoFocus((success, camera) -> {
            mIsFocusing = false;
            if (onFocusListener != null) {
                onFocusListener.onFocusResult(success);
            }
            mCameraSensorListener.onCameraFocused();
        });
    }

    public interface OnPictureTakenListener {
        /**
         * A callback witch called after picture is taken.
         *
         * @param picturePath the decoded picture
         */
        void onPictureTaken(String picturePath);
    }

    @Documented
    @Retention(RetentionPolicy.CLASS)
    @IntDef({Camera.CameraInfo.CAMERA_FACING_FRONT, Camera.CameraInfo.CAMERA_FACING_BACK})
    @interface CameraFacing {
    }

    public interface OnErrorListener {
        /**
         * A callback witch called after preview created.
         */
        void onError(int errorCode);
    }


    public interface OnFocusListener {
        /**
         * Called before {@link Camera#autoFocus(Camera.AutoFocusCallback)}.
         */
        void onFocusStart(Rect focusRect);

        /**
         * Called after Camera focus result.
         *
         * @param success focus success or not.
         */
        void onFocusResult(boolean success);
    }

    public void bindToLifecycle(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        mCameraSensorListener.onStart();
        mOrientationListener.enable();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        mCameraSensorListener.onStop();
        mOrientationListener.disable();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onDestroyed() {
        if (mPreview != null) {
            mPreview.setSurfaceTextureListener(null);
            mPreview.setOnTouchListener(null);
        }
        releaseCamera();
        mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        mOnErrorListener.internalListener = null;
        onFocusListener = null;
        onPreviewListener = null;
        mPreview = null;
        mSurfaceTexture = null;
        mWidth = 0;
        mHeight = 0;
        mErrorCode = 0;
        mIsFocusing = false;
        mIsTakingPicture.set(false);
        mIsPreviewing.set(false);
        mWaitForSurfaceAvailable = true;
    }

    private class ErrorListenerWrapper implements OnErrorListener {
        OnErrorListener internalListener;

        @Override
        public void onError(int errorCode) {
            mErrorCode = errorCode;
            if (errorCode != 0 && internalListener != null) {
                internalListener.onError(errorCode);
            }
        }

        private void wrap(OnErrorListener onErrorListener) {
            this.internalListener = onErrorListener;
        }
    }
}

