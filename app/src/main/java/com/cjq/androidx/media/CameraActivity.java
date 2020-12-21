package com.cjq.androidx.media;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cjq.androidx.GlideApp;
import com.cjq.androidx.R;
import com.cjq.androidx.activity.BigBaseActivity;
import com.cjq.androidx.view.CountDownView;

import java.io.File;

/**
 * An activity used to preview camera, take picture or record video.
 * Note, this class doesn't check the permissions it used, such as {@link Manifest.permission#CAMERA},
 * {@link Manifest.permission#RECORD_AUDIO} and {@link Manifest.permission#WRITE_EXTERNAL_STORAGE},
 * you must ensure these permissions has been granted before user entered the activity.
 * <p>
 * <br/>
 * You may use it like this:
 * <pre>
 *    Intent intent = new Intent(context, CameraActivity.class);
 * // intent.setAction(CameraActivity.ACTION_TAKE_PICTURE); // only want to take picture
 * // intent.setAction(CameraActivity.ACTION_RECORD_VIDEO); // only want to record video
 * // intent.setAction(CameraActivity.ACTION_BOTH); // take picture and record video, can be omit, the default is it.
 *    intent.putExtra(CameraActivity.EXTRA_MEDIA_PATH, path); // can be omit, will auto generate a path
 *    context.startActivityForResult(intent, MY_REQUEST_CODE);
 * </pre>
 * And you also may get the result like this:
 * <pre>
 * protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
 *   super.onActivityResult(requestCode, resultCode, data);
 *   if (requestCode == MY_REQUEST_CODE) {
 *     if (resultCode == Activity.RESULT_OK) {
 *       assert data != null;
 *       String path = data.getStringExtra(CameraActivity.EXTRA_MEDIA_PATH);
 *       ...
 *       // the path is the result file.
 *     } else if (resultCode == Activity.RESULT_CANCELED) {
 *       assert data != null;
 *       int errorCode = data.getIntExtra(CameraActivity.EXTRA_ERROR_CODE, 0);
 *       ...
 *       // the errorCode is defined in CameraManager
 *     } else if (resultCode == Activity.RESULT_FIRST_USER) {
 *       // user pressed the back button
 *     }
 *   }
 * }
 * </pre>
 * @author WingHawk
 */
public class CameraActivity
        extends BigBaseActivity
        implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        CameraManager.OnPictureTakenListener,
        ValueAnimator.AnimatorUpdateListener {

    public static final String ACTION_TAKE_PICTURE = "com.hawk.media.ACTION_TAKE_PICTURE";
    public static final String ACTION_RECORD_VIDEO = "com.hawk.media.ACTION_RECORD_VIDEO";
    public static final String ACTION_BOTH = "com.hawk.media.ACTION_TAKE_PICTURE_AND_RECORD_VIDEO";
    public static final String EXTRA_MEDIA_PATH = "extra_media_path";
    public static final String EXTRA_ERROR_CODE = "extra_error_code";
    public static final String EXTRA_VIDEO_SIZE = "extra_media_size";
    public static final String EXTRA_NOTIFY_FILE_MANAGER = "extra_add_to_file_manager";
    public static final String EXTRA_DEVICE_ROTATION = "extra_device_rotation";

    private static final String KEY_CAMERA_FACING = "key_camera_facing";
    public static final int MAX_RECORD_TIME = 15;

    private CountDownView mBtnStart;
    private View mBtnReplay;
    private View mBtnSwitchCamera;
    private View mBtnCancel;
    private View mBtnConfirm;
    private ImageView mIvPicture;
    private RadioGroup mRgActionType;
    private TextureView mPreview;
    private TextureView mReplayView;
    private String mPicturePath;//拍照路径
    private String mTempPath;//拍视频路径
    private String mMediaPath;//输出路径
    private String mIntentAction;

    private Player mPlayer;
    private Recorder mRecorder;
    private ValueAnimator mConfirmAnimator;
    private SurfaceTexture mReplaySurface;
    private CameraManager mCameraManager;
    private MutableLiveData<Boolean> mActionTypeTrigger;
    private View mTvCancelLabel;
    private View mTvConfirmLabel;
    private CamcorderProfile mCamcorderProfile;
    private View mBtnBack;
    private boolean mNeedResumePlay;
    private boolean mNotifyFileManager;
    private int mDeviceRotation;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraManager = CameraManager.getInstance(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        // add ACTION_VIEW to fix bug when start activity use android_util_code library.
        mIntentAction = action == null || action.equals(Intent.ACTION_VIEW) ? ACTION_BOTH : action;
        // make the volume buttons change STREAM_MUSIC.
        getWindow().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mActionTypeTrigger = new MutableLiveData<>();
        // default is take picture
        mActionTypeTrigger.setValue(false);

        mReplayView = findViewById(R.id.replay);
        mBtnStart = findViewById(R.id.btnStart);
        mBtnReplay = findViewById(R.id.btnReplay);
        mBtnBack = findViewById(R.id.btnBack);
        mBtnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        mBtnCancel = findViewById(R.id.btnCancel);
        mBtnConfirm = findViewById(R.id.btnConfirm);
        mPreview = findViewById(R.id.preview);
        mIvPicture = findViewById(R.id.ivPicture);
        mRgActionType = findViewById(R.id.rgActionType);
        mTvCancelLabel = findViewById(R.id.tvCancelLabel);
        mTvConfirmLabel = findViewById(R.id.tvConfirmLabel);
        mActionTypeTrigger.observe(this, this::switchActionType);
        mRgActionType.setOnCheckedChangeListener(this);
        mBtnSwitchCamera.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnReplay.setOnClickListener(this);
        mBtnStart.setOnClickListener(this);
        mNotifyFileManager = intent.getBooleanExtra(EXTRA_NOTIFY_FILE_MANAGER, false);
        mMediaPath = intent.getStringExtra(EXTRA_MEDIA_PATH);
        switch (mIntentAction) {
            case ACTION_TAKE_PICTURE:
                mRgActionType.setVisibility(View.INVISIBLE);
                mActionTypeTrigger.setValue(false);
                break;
            case ACTION_RECORD_VIDEO:
                mRgActionType.setVisibility(View.INVISIBLE);
                mActionTypeTrigger.setValue(true);
                break;
            default:
                break;
        }
        mCameraManager.setPreview(mPreview);
        mCameraManager.bindToLifecycle(this);
        // restore camera facing
        int cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        if (savedInstanceState != null) {
            cameraFacing = savedInstanceState.getInt(KEY_CAMERA_FACING);
            mCameraManager.setCameraFacing(cameraFacing);
        }
        if (mMediaPath != null) {
            if (!FileUtils.createOrExistsFile(mMediaPath)) {
                // can not create file at the path, maybe path is invalid or no permission, etc...
                setResultErrorAndExit(CameraManager.ERROR_INVALID_PATH);
                return;
            }
            if (FileUtils.getFileLength(mMediaPath) == 0) {
                FileUtils.delete(mMediaPath);
            }
        }
        mReplayView.setSurfaceTextureListener(new ReplaySurfaceTextureListener());
        mCameraManager.setOnPreviewListener(new CameraManager.OnPreviewListener() {
            @Override
            public void onPreviewStarted() {
                mPreview.setVisibility(View.VISIBLE);
                Point previewSize = mCameraManager.getPreviewSize();
                int previewWidth = Math.min(previewSize.x, previewSize.y);
                int previewHeight = Math.max(previewSize.x, previewSize.y);
                float aspectRatio = previewWidth * 1f / previewHeight;
                float newHeight = mPreview.getWidth() / aspectRatio;
                ViewGroup.LayoutParams lpPreview = mPreview.getLayoutParams();
                ViewGroup.LayoutParams lpPicture = mIvPicture.getLayoutParams();
                lpPreview.height = (int) newHeight;
                lpPicture.height = (int) newHeight;
                mPreview.setLayoutParams(lpPreview);
                mIvPicture.setLayoutParams(lpPicture);
            }

            @Override
            public void onPreviewStopped() {
            }
        });

        String[] permissions = new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };
        if (!PermissionUtils.isGranted(permissions)) {
            int finalCameraFacing = cameraFacing;
            PermissionUtils.permission(permissions)
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            initErrorListener();
                            mCameraManager.startPreview(finalCameraFacing);
                        }

                        @Override
                        public void onDenied() {
                            setResultErrorAndExit(CameraManager.ERROR_NO_PERMISSION);
                        }
                    }).request();
        } else {
            int errorCode = mCameraManager.startPreview(cameraFacing);
            if (errorCode == CameraManager.ERROR_NO_PERMISSION) {
                setResultErrorAndExit(CameraManager.ERROR_NO_PERMISSION);
            } else {
                initErrorListener();
            }
        }
        mBtnStart.getCountStatus().observe(this, this::onCountStatusChanged);
    }

    private void onCountStatusChanged(Integer status) {
        if (status == CountDownView.STATUS_END) {
            stopRecord();
        }
    }

    private void initErrorListener() {
        mCameraManager.setOnErrorListener(errorCode -> {
            if (errorCode == CameraManager.ERROR_CAN_NOT_STOP_RECORDER) {
                long recordTime = mRecorder.getRecordTime();
                int minRecordTime = 2000;
                if (recordTime < minRecordTime) {
                    ToastUtils.showShort(R.string.record_time_too_short);
                } else {
                    setResultErrorAndExit(errorCode);
                }
            } else {
                setResultErrorAndExit(errorCode);
            }
        });
    }

    private void setResultErrorAndExit(int errorCode) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ERROR_CODE, errorCode);
        intent.putExtra(EXTRA_MEDIA_PATH, mMediaPath);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void play() {
        mPreview.setVisibility(View.INVISIBLE);
        if (mPlayer == null) {
            mPlayer = new Player(this);
            getLifecycle().addObserver(mPlayer);
        }
        if (!mPlayer.isPlaying()) {
            mPlayer.play(mTempPath, mReplaySurface, mp -> {
                mIvPicture.setVisibility(View.VISIBLE);
                mBtnReplay.setVisibility(View.VISIBLE);
            });
        }
    }

    private void stopRecord() {
        // FIXME: 2018/11/14 stop record in worker thread to avoid ui thread blocking.
        mRecorder.stopRecord();
        mBtnStart.cancel();
        int errorCode = mCameraManager.getErrorCode();
        if (errorCode == 0) {
            showVideoReplay();
        } else {
            mBtnSwitchCamera.setVisibility(View.VISIBLE);
            mRgActionType.setVisibility(mIntentAction.equals(ACTION_BOTH) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void showVideoReplay() {
        mCameraManager.stopPreview();
        showConfirm();
        mPreview.setVisibility(View.INVISIBLE);
        mBtnReplay.setVisibility(View.VISIBLE);
        mIvPicture.setVisibility(View.VISIBLE);
        GlideApp.with(mIvPicture)
                .load(mTempPath)
                .frame(0)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        int frameWidth = resource.getIntrinsicWidth();
                        int frameHeight = resource.getIntrinsicHeight();
                        float aspectRatio = frameWidth * 1f / frameHeight;
                        int newHeight = (int) (mReplayView.getWidth() / aspectRatio);
                        ViewGroup.MarginLayoutParams lpPicture = (ViewGroup.MarginLayoutParams) mIvPicture.getLayoutParams();
                        ViewGroup.MarginLayoutParams lpReplay = (ViewGroup.MarginLayoutParams) mReplayView.getLayoutParams();
                        int bottomHeight = SizeUtils.dp2px(100);
                        int contentHeight = findViewById(android.R.id.content).getHeight();
                        int topMargin = (contentHeight - bottomHeight - newHeight) / 2;
                        lpReplay.height = newHeight;
                        lpReplay.topMargin = topMargin;
                        lpPicture.height = newHeight;
                        lpPicture.topMargin = topMargin;
                        mIvPicture.setLayoutParams(lpPicture);
                        mReplayView.setLayoutParams(lpReplay);
                        return false;
                    }
                }).into(mIvPicture);
    }

    private void startRecord() {
        mRgActionType.setVisibility(View.INVISIBLE);
        mBtnSwitchCamera.setVisibility(View.INVISIBLE);
        mBtnBack.setVisibility(View.INVISIBLE);

        if (mRecorder == null || mRecorder.isDestroyed()) {
            mRecorder = new Recorder(mCameraManager);
        }
        mTempPath = getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".mp4";
        mRecorder.startRecord(mTempPath);
        if (mRecorder.isRecording()) {
            mBtnStart.startCount(MAX_RECORD_TIME + 1);
            mCamcorderProfile = mRecorder.getProfile();
        }
        mDeviceRotation = mCameraManager.getDeviceRotation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CAMERA_FACING, mCameraManager.getCameraFacing());
    }

    @SuppressLint("WrongConstant")
    private void save() {
        if (mMediaPath == null) {
            String fileExtension = "";
            final boolean takePicture = isTakePicture();
            if (takePicture) {
                fileExtension = ".jpg";
            } else {
                if (mRecorder != null) {
                    int fileFormat = mRecorder.fileFormat;
                    switch (fileFormat) {
                        case MediaRecorder.OutputFormat.AAC_ADTS:
                            fileExtension = ".aac";
                            break;
                        case MediaRecorder.OutputFormat.AMR_NB:
                        case MediaRecorder.OutputFormat.AMR_WB:
                            fileExtension = ".amr";
                            break;
                        case MediaRecorder.OutputFormat.MPEG_2_TS:
                            fileExtension = ".mp2";
                            break;
                        case MediaRecorder.OutputFormat.MPEG_4:
                            fileExtension = ".mp4";
                            break;
                        case MediaRecorder.OutputFormat.THREE_GPP:
                            fileExtension = ".3gp";
                            break;
                        case MediaRecorder.OutputFormat.WEBM:
                            fileExtension = ".webm";
                            break;
                        default:
                            fileExtension = ".mp4";
                            break;
                    }
                }
            }
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                // sdcard is mounted
                String[] permissions = new String[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    permissions = new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                }
                if (PermissionUtils.isGranted(permissions)) {
                    saveMediaOnSdcard(fileExtension);
                } else {
                    final String finalFileExtension = fileExtension;
                    PermissionUtils.permission(permissions)
                            .callback(new PermissionUtils.SimpleCallback() {
                                @Override
                                public void onGranted() {
                                    saveMediaOnSdcard(finalFileExtension);
                                }

                                @Override
                                public void onDenied() {
                                    saveResultOnFilesDir(finalFileExtension);
                                }
                            }).request();
                }
            } else {
                saveResultOnFilesDir(fileExtension);
            }
        } else {
            saveMediaFile();
        }
    }

    private void saveResultOnFilesDir(String fileExtension) {
        File dir = getFilesDir();
        mMediaPath = new File(dir, System.currentTimeMillis() + fileExtension).getAbsolutePath();
        saveMediaFile();
    }

    private void saveMediaOnSdcard(String fileExtension) {
        File dir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/");
        FileUtils.createOrExistsDir(dir);
        mMediaPath = new File(dir, System.currentTimeMillis() + fileExtension).getPath();
        saveMediaFile();
    }

    private void switchActionType(@Nullable Boolean isRecordVideo) {
        isRecordVideo = isRecordVideo != null && isRecordVideo;
        mBtnStart.setActivated(isRecordVideo);
        mCameraManager.focus(mPreview.getWidth() / 2, mPreview.getHeight() / 2);
        mCameraManager.setRecordingHint(isRecordVideo);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_FIRST_USER, null);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnSwitchCamera:
                mCameraManager.toggleCameraFacing();
                break;
            case R.id.btnCancel:
                hideConfirms();
                mCameraManager.restartPreview();
                stopReplay();
                break;
            case R.id.btnConfirm:
                save();
                stopReplay();
                break;
            case R.id.btnReplay:
                replay();
                break;
            case R.id.btnStart:
                if (isTakePicture()) {
                    mCameraManager.takePicture(this);
                    mDeviceRotation = mCameraManager.getDeviceRotation();
                } else {
                    switchRecord();
                }
            default:
                break;
        }
    }

    private void switchRecord() {
        if (mRecorder != null && mRecorder.isRecording()) {
            stopRecord();
        } else {
            startRecord();
        }
    }

    private void saveMediaFile() {
        boolean takePicture = isTakePicture();
        if (takePicture) {
            boolean saveSuccess = FileUtils.move(mPicturePath, mMediaPath);
            onSaveResult(saveSuccess);
        } else {
            // save recorded video file
            final boolean saveSuccess = FileUtils.copy(mTempPath, mMediaPath);
            onSaveResult(saveSuccess);
        }
    }

    private void onSaveResult(boolean saveSuccess) {
        boolean takePicture = isTakePicture();
        if (!saveSuccess) {
            setResultErrorAndExit(CameraManager.ERROR_CAN_NOT_SAVE_MEDIA_FILE);
        } else {
            if (mNotifyFileManager) {
                MediaScannerConnection.scanFile(Utils.getApp(), new String[]{mMediaPath}, new String[]{"video/*", "image/*"}, null);
            }
            Intent intent = new Intent();
            intent.setAction(takePicture ? ACTION_TAKE_PICTURE : ACTION_RECORD_VIDEO);
            intent.putExtra(EXTRA_MEDIA_PATH, mMediaPath);
            intent.putExtra(EXTRA_DEVICE_ROTATION, mDeviceRotation);
            if (!takePicture) {
                intent.putExtra(EXTRA_VIDEO_SIZE, mCamcorderProfile.videoFrameWidth + "," + mCamcorderProfile.videoFrameHeight);
            }
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private void stopReplay() {
        mReplayView.setVisibility(View.INVISIBLE);
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    private void replay() {
        mCameraManager.stopPreview();
        if (mReplaySurface != null) {
            play();
        }
        mReplayView.setVisibility(View.VISIBLE);
        mBtnReplay.setEnabled(false);
        mReplayView.postDelayed(() -> {
            mBtnReplay.setEnabled(true);
            mBtnReplay.setVisibility(View.INVISIBLE);
            mIvPicture.setVisibility(View.INVISIBLE);
        }, 300);
    }

    private void showConfirm() {
        if (mConfirmAnimator == null) {
            mConfirmAnimator = ValueAnimator.ofFloat(0, mBtnStart.getWidth() * 2.5f);
            mConfirmAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            int animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mConfirmAnimator.setDuration(animTime);
            mConfirmAnimator.addUpdateListener(this);
        }
        mConfirmAnimator.removeAllListeners();
        mConfirmAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTvCancelLabel.setVisibility(View.VISIBLE);
                mTvConfirmLabel.setVisibility(View.VISIBLE);
            }
        });
        mRgActionType.setVisibility(View.INVISIBLE);
        mBtnStart.setVisibility(View.INVISIBLE);
        mBtnConfirm.setVisibility(View.VISIBLE);
        mBtnCancel.setVisibility(View.VISIBLE);
        mConfirmAnimator.start();
        mBtnSwitchCamera.setVisibility(View.INVISIBLE);
    }

    private void hideConfirms() {
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnReplay.setVisibility(View.INVISIBLE);
        mIvPicture.setVisibility(View.INVISIBLE);
        mPreview.setVisibility(View.VISIBLE);
        mConfirmAnimator.removeAllListeners();
        mTvCancelLabel.setVisibility(View.INVISIBLE);
        mTvConfirmLabel.setVisibility(View.INVISIBLE);
        mConfirmAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBtnStart.setVisibility(View.VISIBLE);
                mBtnCancel.setVisibility(View.INVISIBLE);
                mBtnConfirm.setVisibility(View.INVISIBLE);
                mRgActionType.setVisibility(mIntentAction.equals(ACTION_BOTH) ? View.VISIBLE : View.INVISIBLE);
            }
        });
        mConfirmAnimator.reverse();
        mBtnSwitchCamera.setVisibility(View.VISIBLE);
    }

    private boolean isTakePicture() {
        Boolean value = mActionTypeTrigger.getValue();
        return value == null || !value;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mActionTypeTrigger.setValue(checkedId == R.id.rbRecordVideo);
    }

    @Override
    public void onPictureTaken(String picturePath) {
        mPicturePath = picturePath;
        showConfirm();
        GlideApp.with(mIvPicture).load(picturePath).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                mIvPicture.setVisibility(View.VISIBLE);
                mPreview.setVisibility(View.INVISIBLE);
                return false;
            }
        }).into(mIvPicture);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // change view's margin to let it animate
        Float value = (Float) animation.getAnimatedValue();
        ConstraintLayout.LayoutParams lpBtnConfirm = (ConstraintLayout.LayoutParams) mBtnConfirm.getLayoutParams();
        lpBtnConfirm.leftMargin = value.intValue();
        mBtnConfirm.setLayoutParams(lpBtnConfirm);
        ConstraintLayout.LayoutParams lpBtnCancel = (ConstraintLayout.LayoutParams) mBtnCancel.getLayoutParams();
        lpBtnCancel.rightMargin = value.intValue();
        mBtnCancel.setLayoutParams(lpBtnCancel);
    }

    private class ReplaySurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mReplaySurface = surface;
            if (mPlayer == null) {
                play();
            } else if (mNeedResumePlay) {
                mPlayer.setSurface(new Surface(surface));
                mPlayer.resume();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mReplaySurface = null;
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
                mNeedResumePlay = true;
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean isRecording = mRecorder != null && mRecorder.isRecording();
        if (isFinishing()) {
            if (isRecording) {
                mRecorder.stopRecord();
                mRecorder.release(true);
            }
            if (mConfirmAnimator != null && mConfirmAnimator.isStarted()) {
                mConfirmAnimator.cancel();
            }
            mCameraManager.onDestroyed();
        }
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };
    }
}