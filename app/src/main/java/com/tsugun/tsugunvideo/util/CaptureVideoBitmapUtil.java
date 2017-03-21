package com.tsugun.tsugunvideo.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.tsugun.tsugunvideo.R;

/**
 * 捕获视频内截图
 * Created by shize on 2017/3/3.
 */

public class CaptureVideoBitmapUtil {

    /**
     * 获取指定大小的截图
     *
     * @param context  上下文
     * @param filePath 视频文件路径
     * @param height   文件高度
     * @param width    文件宽度
     * @param kind     截取模式MINI_KIND（512 x 384）或MICRO_KIND（96 x 96）模式
     * @return 截取完成的图片
     */
    public static Bitmap getVideoBitmap(Context context, String filePath, int height, int width, int kind) {
        // 获取视频截图
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);
        // 将截图转换为指定大小
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        if (bitmap == null) {
            Resources res = context.getResources();
            bitmap = BitmapFactory.decodeResource(res, R.drawable.bg_image_title);
        }
        return bitmap;
    }

    /**
     * 获取指定大小的截图
     *
     * @param filePath 视频文件路径
     * @param height   文件高度
     * @param width    文件宽度
     * @param kind     截取模式MINI_KIND（512 x 384）或MICRO_KIND（96 x 96）模式
     * @return 截取完成的图片
     */
    public static Bitmap getVideoBitmap(String filePath, int height, int width, int kind) {
        // 获取视频截图
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);
        // 将截图转换为指定大小
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 直接通过路径截取视频文件图片
     *
     * @param path 视频文件路径
     * @return 图片
     */
    public static Bitmap getVideoBitmap(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        Log.i("Wang", "getVideoBitmap: " + path);
        Bitmap bitmap = retriever.getFrameAtTime(-1);
        if (bitmap != null) {
            Log.i("Wang", "getVideoBitmap: " + bitmap);
        }
        retriever.release();
        return bitmap;
    }

}
