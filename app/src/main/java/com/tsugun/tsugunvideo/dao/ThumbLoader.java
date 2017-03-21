package com.tsugun.tsugunvideo.dao;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 缩略图加载接口
 * Created by shize on 2017/3/3.
 */

public interface ThumbLoader {

    /**
     * 获取视频缩略图
     *
     * @param path      视频路径
     * @param imageView 显示图片view
     */
    void getVideoThumb(String path, ImageView imageView);

    /**
     * 添加缩略图到内存中
     *
     * @param path 视频文件路径
     * @param thumb 缩略图
     */
    void addThumbToCache(String path, Bitmap thumb);

    /**
     * 从缓存中获取缩略图
     * @param path 视频文件路径
     * @return 缩略图
     */
    Bitmap getThumbByCache(String path);

}
