package com.cjq.androidx.tools;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class AssetsFileUtils {
    public static <T> T parseAssetJson(String assetFileName, Class<T> clazz) {
        try (InputStream in = Utils.getApp().getAssets().open(assetFileName)) {
            InputStreamReader reader = new InputStreamReader(in);
            StringWriter writer = new StringWriter();
            char[] buf = new char[1024];
            int len;
            while ((len = reader.read(buf)) != -1) {
                writer.write(buf, 0, len);
            }
            String json = writer.getBuffer().toString();
            return JSON.parseObject(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
