package com.tsugun.tsugunvideo.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.LruCache;
import android.widget.ImageView;

import com.tsugun.tsugunvideo.activity.MainActivity;
import com.tsugun.tsugunvideo.dao.ThumbLoader;
import com.tsugun.tsugunvideo.entity.MediaFileInfo;

import java.util.HashMap;
import java.util.List;

import static com.tsugun.tsugunvideo.activity.MainActivity.VIDEO_PATH_LIST_UPDATE;

/**
 * 缩略图加载器
 * Created by shize on 2017/3/3.
 */

public class TsuGunThumbLoader implements ThumbLoader {

    private LruCache<String, Bitmap> mLruCache;
    private HashMap<String, ImageView> imageViewHashMap;
    private boolean isFirst;
    private List<MediaFileInfo> mInfoList;

    public TsuGunThumbLoader(List<MediaFileInfo> infoList) {
        imageViewHashMap = new HashMap<>();
        mInfoList = infoList;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxSize = maxMemory / 4;// 获取最大内存的1/4
        mLruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        isFirst = true;
    }

    @Override
    public void getVideoThumb(String path, ImageView imageView) {
        imageViewHashMap.put(path, imageView);
        if (isFirst){
            new VideoThumbAsyncTask().execute(mInfoList);
            isFirst = false;
        }
        if (getThumbByCache(path) != null) {
            imageView.setImageBitmap(getThumbByCache(path));
        }
    }

    @Override
    public void addThumbToCache(String path, Bitmap thumb) {
        mLruCache.put(path, thumb);
    }

    @Override
    public Bitmap getThumbByCache(String path) {
        if (imageViewHashMap.get(path).getTag() == path) {
            return mLruCache.get(path);
        }
        return null;
    }

    private class VideoThumbAsyncTask extends AsyncTask<List<MediaFileInfo>, Object, Void> {

        @Override
        protected Void doInBackground(List<MediaFileInfo>... params) {
            // 实现多线程同时加载图片
            for (final MediaFileInfo info : params[0]) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap thumb = CaptureVideoBitmapUtil.getVideoBitmap(info.getUrl(), 180, 320,
                                MediaStore.Images.Thumbnails.MINI_KIND);
                        publishProgress(info.getUrl(), thumb);
                    }
                }).start();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            String path = (String) values[0];
            Bitmap bitmap = (Bitmap) values[1];
            addThumbToCache(path, bitmap);
            ImageView v = imageViewHashMap.get(path);
            if (v != null) {
                v.setImageBitmap(getThumbByCache(path));
                MainActivity.mVideoUIHandler.sendEmptyMessage(VIDEO_PATH_LIST_UPDATE);
            }
        }
    }

}
