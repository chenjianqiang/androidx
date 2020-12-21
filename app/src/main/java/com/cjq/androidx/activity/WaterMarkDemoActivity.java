package com.cjq.androidx.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cjq.androidx.GlideApp;
import com.cjq.androidx.R;
import com.cjq.androidx.databinding.ActivityWatermarkDemoBinding;
import com.cjq.androidx.media.CameraActivity;
import com.cjq.androidx.media.FFmpegUtils;
import com.cjq.androidx.media.WindowOrientationListener;
import com.cjq.androidx.view.WatermarkContainerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class WaterMarkDemoActivity extends BigBaseActivity {
    private static final int REQUEST_WATERMARK_DEMO = 11;
    private static final int CIRCLE_DEGREES = 360;

    private View mContentView;
    private WatermarkContainerView mWatermarkContainer;
    private View mViewTopBanner;
    private ImageView mIvPicture;
    private View mViewWatermark;
    private View mBtnSavePicture;
    private TextView mTvAddress;
    private Chronometer mChronometer;

    private String mMediaPath;

    private DeviceRotationListener mDeviceRotationListener;
    private int mDeviceRotation;
    private int mWatermarkRotation;
    private boolean mIsLandMedia;
    private boolean mIsPicture;


    private AsyncTask<Void, Void, String> mWatermarkTask;
    private String mWatermarkPath;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watermark_demo);
        navToCameraActivity();
    }

    private void navToCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        // only want to record video
        // intent.setAction(CameraActivity.ACTION_RECORD_VIDEO);
        // can be omit, will auto generate a path
        // intent.putExtra(CameraActivity.EXTRA_MEDIA_PATH, "outputFilePath");
        startActivityForResult(intent, REQUEST_WATERMARK_DEMO);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        processCameraResult(requestCode, resultCode, data);
    }


    private void processCameraResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_WATERMARK_DEMO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    mMediaPath = data.getStringExtra(CameraActivity.EXTRA_MEDIA_PATH);
                    int deviceRotation = data.getIntExtra(CameraActivity.EXTRA_DEVICE_ROTATION, 0);
                    if (mIvPicture == null) {
                        mIvPicture = findViewById(R.id.ivPicture);
                    }
                    mIsPicture = CameraActivity.ACTION_TAKE_PICTURE.equals(data.getAction());
                    if (mViewWatermark != null) {
                        mViewWatermark.setVisibility(View.INVISIBLE);
                    }
                    if (mIsPicture) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(mMediaPath, options);
                        int screenWidth = ScreenUtils.getScreenWidth();
                        int screenHeight = ScreenUtils.getScreenHeight();
                        int width = options.outWidth;
                        int height = options.outHeight;
                        float aspectRatio = width * 1f / height;
                        int bitmapWidth = Math.max(width > height ? screenHeight : screenWidth, 720);
                        int bitmapHeight = (int) (bitmapWidth / aspectRatio);
                        GlideApp.with(mIvPicture)
                                .asBitmap()
                                .load(mMediaPath)
                                .override(bitmapWidth, bitmapHeight)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        setupShowImage(resource, deviceRotation);
                                        return true;
                                    }
                                }).into(mIvPicture);
                    } else {
                        GlideApp.with(mIvPicture)
                                .asBitmap()
                                .frame(0)
                                .load(mMediaPath)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        setupShowImage(resource, deviceRotation);
                                        return true;
                                    }
                                }).into(mIvPicture);
                    }
                } else {
                    exitActivity();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    int errorCode = data.getIntExtra(CameraActivity.EXTRA_ERROR_CODE, 0);
                    if (errorCode != 0) {
                        ToastUtils.showShort("操作失败，错误码：" + errorCode);
                    }
                }
                exitActivity();
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                // user finished camera activity.
                exitActivity();
            }
        }
    }

    private void exitActivity() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSavePicture:
                mBtnSavePicture.setEnabled(false);
                exeAddWaterMarker();
                break;
            case R.id.btnClose:
                finish();
                break;
            case R.id.btnRetakePicture:
                navToCameraActivity();
                break;
            default:
                break;
        }
    }

    private void setupShowImage(Bitmap bitmap, int deviceRotation) {
        showAddWatermark();
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        mIsLandMedia = bw > bh;
        int pictureWidth = Math.min(bw, bh);
        int pictureHeight = Math.max(bw, bh);
        float aspectRatio = pictureWidth * 1f / pictureHeight;
        int width = ScreenUtils.getScreenWidth();
        float newHeight = width / aspectRatio;
        ViewGroup.MarginLayoutParams picLp = (ViewGroup.MarginLayoutParams) mIvPicture.getLayoutParams();
        int parentHeight = ((ViewGroup) mIvPicture.getParent()).getHeight();
        float picTopMargin = (parentHeight - newHeight) / 2;
        picLp.height = (int) newHeight;
        picLp.topMargin = (int) picTopMargin;
        mIvPicture.requestLayout();
        Matrix matrix = new Matrix();
        float sx;
        float sy;
        if (mIsLandMedia) {
            matrix.preRotate(90, 0.5f, 0.5f);
            sx = newHeight * 1f / bw;
            sy = width * 1f / bh;
        } else {
            sx = newHeight * 1f / bh;
            sy = width * 1f / bw;
        }
        matrix.postScale(sx, sy);
        Bitmap picture = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, matrix, true);
        if (mIsPicture) {
            ImageUtils.save(picture, mMediaPath, Bitmap.CompressFormat.JPEG);
        }
        mIvPicture.setImageBitmap(picture);
        int rotation = deviceRotation * 90;
        mWatermarkRotation = rotation % 180 == 90 ? (rotation + 180) % 360 : rotation;
        mViewWatermark.setRotation(mWatermarkRotation);
        mWatermarkContainer.setWatermarkRotation(mWatermarkRotation);
        mViewWatermark.setVisibility(View.VISIBLE);
        int xMargin = SizeUtils.dp2px(16);
        int yMargin = SizeUtils.dp2px(24);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mViewWatermark.getLayoutParams();
        if (!mIsPicture) {
            lp.rightMargin = 0;
            lp.bottomMargin = 0;
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            int topBannerHeight = mViewTopBanner.getHeight();
            int wmWidth = mViewWatermark.getWidth();
            int wmHeight = mViewWatermark.getHeight();
            if (wmWidth == 0 || wmHeight == 0) {
                int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                mViewWatermark.measure(measureSpec, measureSpec);
                wmWidth = mViewWatermark.getMeasuredWidth();
                wmHeight = mViewWatermark.getMeasuredHeight();
            }
            int extra = (wmWidth - wmHeight) / 2;
            int picBottom = (int) (picTopMargin + newHeight);
            switch (deviceRotation) {
                case Surface.ROTATION_0:
                    lp.leftMargin = width - xMargin - wmWidth;
                    lp.topMargin = picBottom - topBannerHeight - wmHeight - yMargin;
                    break;
                case Surface.ROTATION_90:
                    lp.leftMargin = width - xMargin - wmHeight - extra;
                    lp.topMargin = (int) (picTopMargin - topBannerHeight + yMargin + extra);
                    break;
                case Surface.ROTATION_180:
                    lp.leftMargin = xMargin;
                    lp.topMargin = (int) (picTopMargin - topBannerHeight + yMargin);
                    break;
                case Surface.ROTATION_270:
                    lp.leftMargin = xMargin - extra;
                    lp.topMargin = picBottom - topBannerHeight - wmHeight - yMargin - extra;
                    break;
                default:
                    break;
            }
        }
        mViewWatermark.setLayoutParams(lp);
    }


    private void showAddWatermark() {
        if (mContentView == null) {
            mContentView = findViewById(R.id.contentView);
            mContentView.setVisibility(View.VISIBLE);
            mViewWatermark = findViewById(R.id.llWatermark);
            mViewTopBanner = findViewById(R.id.flTopBanner);
            findViewById(R.id.btnClose).setOnClickListener(this);
            mBtnSavePicture = findViewById(R.id.btnSavePicture);
            mBtnSavePicture.setOnClickListener(this);
            findViewById(R.id.btnRetakePicture).setOnClickListener(this);
            mWatermarkContainer = findViewById(R.id.rlPictureContainer);
            TextView mTvFarmerName = findViewById(R.id.tvMapperName);
            mTvFarmerName.setText("湖北省武汉市");
            mTvAddress = findViewById(R.id.tvAddress);
            mTvAddress.setText("湖北省武汉市东湖高新开发区");
            mChronometer = findViewById(R.id.chronometer);
            TextView mTvDate = findViewById(R.id.tvDate);
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
            String date = mDateFormat.format(new Date());
            mTvDate.setText(date);
            mDeviceRotationListener = new DeviceRotationListener(this);
            mDeviceRotation = getWindowManager().getDefaultDisplay().getRotation();
            mDeviceRotationListener.setCurrentRotation(mDeviceRotation);
        }
        mWatermarkContainer.setDragEnabled(mIsPicture);
        mDeviceRotationListener.enable();
        startChronometer();
    }

    private void startChronometer() {
        // Use chronometer instead of call *System.currentTimeMills()* and format it every second,
        // because that's a weight and time-costing operation.
        GregorianCalendar calendar = new GregorianCalendar();
        GregorianCalendar today = new GregorianCalendar(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        long currentTime = calendar.getTimeInMillis();
        long todayStartTime = today.getTimeInMillis();
        long todayElapsedTime = currentTime - todayStartTime;
        long base = SystemClock.elapsedRealtime() - todayElapsedTime;
        mChronometer.setBase(base);
        mChronometer.start();
    }

    private class DeviceRotationListener extends WindowOrientationListener {

        /**
         * Creates a new WindowOrientationListener.
         *
         * @param context for the WindowOrientationListener.
         */
        DeviceRotationListener(Context context) {
            super(context);
        }

        @Override
        public void onProposedRotationChanged(int rotation) {
            int currentRotation = (int) mViewWatermark.getRotation();
            if (rotation - mDeviceRotation == -1 || rotation - mDeviceRotation == Surface.ROTATION_270) {
                // anti-clockwise
                currentRotation -= 90;
            } else if (rotation - mDeviceRotation == 1 || rotation - mDeviceRotation == -Surface.ROTATION_270) {
                // clockwise
                currentRotation += 90;
            } else {
                currentRotation = rotation * 90;
            }
            mDeviceRotation = rotation;
            if ((mWatermarkRotation + CIRCLE_DEGREES) % CIRCLE_DEGREES ==
                    (currentRotation + CIRCLE_DEGREES) % CIRCLE_DEGREES) {
                return;
            }

            mWatermarkRotation = currentRotation % 360;
            if (mIsPicture) {
                boolean dragEnabled = mWatermarkContainer.isDragEnabled();
                mViewWatermark.animate()
                        .rotation(mWatermarkRotation)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mWatermarkContainer.setDragEnabled(dragEnabled);
                                mWatermarkContainer.adjustBounds(mViewWatermark);
                            }
                        })
                        .start();
                mWatermarkContainer.setDragEnabled(false);
                mWatermarkContainer.setWatermarkRotation(mWatermarkRotation);
            }
        }
    }

    /**
     * 添加水印并保存
     */
    private void exeAddWaterMarker() {
        long current = System.currentTimeMillis();
        if (mIsPicture) {
            //Bitmap watermark = mWatermarkContainer.getDrawingCache();
            Bitmap watermark = BitmapFactory.decodeResource(Utils.getApp().getResources(), R.drawable.video_watermark_logo);
            Bitmap srcPic = ImageUtils.getBitmap(mMediaPath);
            Bitmap watermarkedPicture = createWatermarkedPicture(srcPic, watermark);
            if (ImageUtils.save(watermarkedPicture, getExternalCacheDir() + File.separator + current + "_watermark_suc.png", Bitmap.CompressFormat.JPEG)) {
                ToastUtils.showShort("加水印成功! 路径在:" + getExternalCacheDir() + File.separator + current + "_watermark_suc.png");
            } else {
                ToastUtils.showShort("加水印失败");
            }
        } else {
            String dstFilePath = getExternalCacheDir() + File.separator + current + "_video_dst.mp4";
            addVideoWatermark(FileUtils.getFileByPath(mMediaPath), FileUtils.getFileByPath(dstFilePath));
        }
    }

    private Bitmap mWatermarkBmp;
    private boolean ensureWatermark(String videoPath) {
        mWatermarkBmp = BitmapFactory.decodeResource(Utils.getApp().getResources(), R.drawable.video_watermark_logo);
        /*int screenWidth = ScreenUtils.getScreenWidth();
        int[] videoSize = getVideoSize(videoPath);
        int videoWidth = Math.min(videoSize[0], videoSize[1]);
        float scaleRate = videoWidth * 1f / screenWidth;
        mWatermarkBmp = ImageUtils.scale(mWatermarkBmp, scaleRate, scaleRate);*/
        mWatermarkPath = new File(getExternalCacheDir(), System.currentTimeMillis() + ".png").getAbsolutePath();
        return ImageUtils.save(mWatermarkBmp, mWatermarkPath, Bitmap.CompressFormat.PNG);
    }

    public int[] getVideoSize(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        retriever.release();
        return new int[]{Integer.parseInt(width), Integer.parseInt(height)};
    }

    private Bitmap createWatermarkedPicture(Bitmap srcPic, Bitmap watermark) {
        int x = srcPic.getWidth() - watermark.getWidth();
        int y = srcPic.getHeight() - watermark.getHeight();
        return ImageUtils.addImageWatermark(srcPic, watermark, x, y, 255);
    }

    private void addVideoWatermark(File src, File dst) {
        if (!ensureWatermark(src.getPath())) {
            Log.e("WaterMarkDemo","addVideoWatermark fail");
            return;
        }
        mWatermarkTask = addVideoWatermarkAsync(src, dst, mWatermarkPath);
    }

    /**
     * add watermark for Video
     */
    private static AsyncTask<Void, Void, String> addVideoWatermarkAsync(
            final File src,
            File dst,
            String watermarkPath) {
        AddVideoWatermarkTask task = new AddVideoWatermarkTask(src, dst, watermarkPath);
        task.execute();
        return task;
    }

    private static class AddVideoWatermarkTask extends AsyncTask<Void, Void, String> {

        private final File src;
        private final File dst;
        private final String watermarkPath;
        private File tmpSrc;
        private File tmpDst;

        AddVideoWatermarkTask(File src, File dst, String watermarkPath) {
            this.src = src;
            this.dst = dst;
            this.watermarkPath = watermarkPath;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // backup
            Application ctx = Utils.getApp();
            long time = System.currentTimeMillis();
            tmpSrc = new File(ctx.getExternalCacheDir(), time + "_wm_src_tmp.mp4");
            tmpDst = new File(ctx.getExternalCacheDir(), time + "_wm_dst_tmp.mp4");
            boolean success = FileUtils.copy(src, tmpSrc);
            if (!success) {
                clear();
                return null;
            }
            Bitmap bitmapLogo = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.video_watermark_logo);
            int[] watermarkSize = ImageUtils.getSize(watermarkPath);
            int logoWidth = watermarkSize[0] / 2;
            float factor = logoWidth * 1f / bitmapLogo.getWidth();
            bitmapLogo = ImageUtils.scale(bitmapLogo, factor, factor);
            String logoPath = new File(ctx.getExternalCacheDir(), time + "_logo.png").getAbsolutePath();
            if (!ImageUtils.save(bitmapLogo, logoPath, Bitmap.CompressFormat.PNG)) {
                return null;
            }
            Log.i("test",
                    ("ffmpeg doInBackground: " +
                            "\nsrc=" + tmpSrc + "  exists=" + FileUtils.isFile(tmpSrc) +
                            "\ndst=" + tmpDst + "  exists=" + FileUtils.isFile(tmpDst) +
                            "\nlogo=" + logoPath + "  exists=" + FileUtils.isFile(logoPath) +
                            "\nwm=" + watermarkPath) + "  exists=" + FileUtils.isFile(watermarkPath));
            return FFmpegUtils.addVideoWaterMark(
                    tmpSrc.getPath(),
                    tmpDst.getPath(),
                    logoWidth / 7,
                    logoWidth / 7,
                    new String[]{logoPath, watermarkPath});
        }

        @Override
        protected void onPostExecute(String result) {
            final boolean success = result != null && FileUtils.move(tmpDst, dst);
            if (success) {
                FileUtils.delete(tmpDst);
            } else {
                clear();
            }
        }

        private void clear() {
            FileUtils.delete(tmpSrc);
            FileUtils.delete(dst);
            FileUtils.delete(tmpDst);
        }
    }
}
