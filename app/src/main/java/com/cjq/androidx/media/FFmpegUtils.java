package com.cjq.androidx.media;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Size;
import androidx.annotation.WorkerThread;

import com.annimon.stream.Stream;
import com.blankj.utilcode.util.FileUtils;
import com.lansosdk.videoeditor.VideoEditor;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.SynchronousQueue;


public class FFmpegUtils {

    @WorkerThread
    public static String syncTranscode(Context context, String srcPath, String dstPath, String format) {
        String tmpDstPath = new File(context.getExternalCacheDir(), System.currentTimeMillis() + "_transcode_tmp." + format).getAbsolutePath();
        String[] commands = {
                "-i", srcPath,
                "-f", format,
                "-vcodec", "lansoh264_dec",
                "-profile:v", "baseline",
                "-preset", "ultrafast"
        };
        List<String> cmdArgs = Arrays.asList(commands);
        VideoEditor videoEditor = new VideoEditor();
        SynchronousQueue<String> transcodeResult = new SynchronousQueue<>();
        AsyncTask.SERIAL_EXECUTOR.execute(() -> {
            String result = "";
            String encodePath = videoEditor.executeAutoSwitch(cmdArgs, tmpDstPath);
            if (encodePath != null) {
                // success
                boolean moveSuccess = FileUtils.move(tmpDstPath, dstPath);
                if (moveSuccess) {
                    result = dstPath;
                }
            }
            try {
                transcodeResult.put(result);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
        try {
            return transcodeResult.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

    @WorkerThread
    public static String addVideoWaterMark(
            String srcPath,
            String dstPath,
            int logoMargin,
            int wmMargin,
            @Size(value = 2) String[] watermarkPath) {
        List<String> cmdArgs = Stream.of(buildWatermarkCmd(srcPath, logoMargin, wmMargin, watermarkPath)).toList();
        VideoEditor videoEditor = new VideoEditor();
        try {
            return videoEditor.executeAutoSwitch(cmdArgs, dstPath);
        } catch (Exception e) {
            String errorLog = VideoEditor.getErrorLog();
            Log.e("test", "addVideoWaterMark: e=" + e);
            Log.e("test", "addVideoWaterMark: " + errorLog);
            return null;
        }
    }

    private static String[] buildWatermarkCmd(
            String videoPath,
            int logoMargin,
            int wmMargin,
            @Size(value = 2) String[] watermarkPath) {
        List<String> commands = new LinkedList<>();
        commands.add("-vcodec");
        commands.add("lansoh264_dec");
        // input
        commands.add("-i");
        commands.add(videoPath);
        // input
        commands.add("-i");
        commands.add(watermarkPath[0]);
        // input
        commands.add("-i");
        commands.add(watermarkPath[1]);
        // video profile
        commands.add("-profile:v");
        commands.add("baseline");
        commands.add("-preset");
        commands.add("ultrafast");
        // add filter
        commands.add("-filter_complex");
        // add second input content to first input content at position 0,0 with logoMargin, and save result as bkg,
        // then add third input content at right-bottom of bkg.
        String format = "[0:v][1:v]overlay=%d:%d[bkg];[bkg][2:v]overlay=W-w-%d:H-h";
        commands.add(String.format(Locale.US, format, logoMargin, logoMargin, wmMargin));
        String[] out = new String[commands.size()];
        return commands.toArray(out);
    }
}
