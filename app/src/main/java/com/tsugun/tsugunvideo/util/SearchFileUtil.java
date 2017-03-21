package com.tsugun.tsugunvideo.util;

import android.media.MediaMetadataRetriever;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tsugun.tsugunvideo.entity.MediaFileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 搜索文件工具类
 * Created by shize on 2017/3/1.
 */

public class SearchFileUtil {

    /**
     * 扫描sdcard查找符合文件的路径
     *
     * @param directory 需要检查的文件
     * @param externals 需要的文件扩展名
     */
    public static List<MediaFileInfo> searchFilePath(File directory, String[] externals) {
        List<MediaFileInfo> fileList1 = new ArrayList<>();
        if (directory != null) {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        for (MediaFileInfo info : searchFilePath(file, externals)) {
                            fileList1.add(info);
                        }
                    }
                }
            } else {
                String filePath = directory.getAbsolutePath();
                for (String ext : externals) {
                    if (filePath.endsWith(ext)) {
                        MediaFileInfo mediaFileInfo = getFileInfo(directory, ext);
                        if (mediaFileInfo != null) {
                            fileList1.add(mediaFileInfo);
                        }
                        break;
                    }
                }
            }
        }
        return fileList1;
    }

    /**
     * 在文件内获取文件信息
     *
     * @param file 文件
     * @return 文件信息
     */
    @Nullable
    private static MediaFileInfo getFileInfo(File file, String ext) {
        MediaFileInfo fileInfo = new MediaFileInfo();
        boolean isError = false;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(file.getAbsolutePath());
            fileInfo.setHeight(Float.parseFloat(retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
            fileInfo.setWidth(Float.parseFloat(retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
            fileInfo.setDuration(Integer.parseInt(retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION)));
            fileInfo.setRotation(Integer.parseInt(retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)));
        } catch (Exception ex) {
            Log.e(TAG, "getFileInfo: " + file.getName() + "----打开文件失败！！！");
            Log.e(TAG, "             " + file.getName() + "----无法打开数据源！！！");
            isError = true;
        } finally {
            retriever.release();
        }
        // 文件无法解析的直接忽略
        if (isError) {
            return null;
        }
        // 视频声音小于一分钟的忽略
        if (fileInfo.getDuration() < 60000) {
            return null;
        }
        fileInfo.setUrl(file.getAbsolutePath());
        fileInfo.setSize(getFileSize(file));
        fileInfo.setType(ext);
        fileInfo.setTitle(file.getName());
        Log.i("Wang0", "height = " + fileInfo.getHeight() + "----width = " + fileInfo.getWidth() +
                "----duration = " + fileInfo.getDuration() + "----rotation = "
                + fileInfo.getRotation());
        return fileInfo;
    }

    /**
     * 获取文件大小
     */
    private static long getFileSize(File file) {
        long size = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "getFileSize: 无法获取文件大小，文件不存在！！！");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getFileSize: 文件大小获取失败！！！");
        }
        return size;
    }
}
