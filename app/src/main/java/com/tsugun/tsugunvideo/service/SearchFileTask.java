package com.tsugun.tsugunvideo.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tsugun.tsugunvideo.dao.MediaFileFinder;
import com.tsugun.tsugunvideo.entity.MediaFileInfo;
import com.tsugun.tsugunvideo.util.SearchFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 查找对应文件服务
 * Created by shize on 2017/3/2.
 */

public class SearchFileTask extends AsyncTask<Void, Integer, List<MediaFileInfo>> {

    private MediaFileFinder mMediaFileFinder;
    private OnSearchCompletionListener mCompletionListener;

    public SearchFileTask(MediaFileFinder mMediaFileFinder) {
        this.mMediaFileFinder = mMediaFileFinder;
    }

    public void setCompletionListener(OnSearchCompletionListener completionListener) {
        mCompletionListener = completionListener;
    }

    @Override
    protected List<MediaFileInfo> doInBackground(Void... params) {
        File file = new File(Environment.getExternalStorageDirectory() + "/");
        final List<MediaFileInfo> info = getInfoFromThread(file);
        // 添加数据进数据库
        mMediaFileFinder.addFileInfo(info);
        return info;
    }

    /**
     * 在子线程中获取信息
     *
     * @param file 文件夹
     * @return 文件信息集合
     */
    @NonNull
    private List<MediaFileInfo> getInfoFromThread(File file) {
        final String[] strExt = new String[]{".mp4", ".3gp"};
        final List<MediaFileInfo> info = new ArrayList<>();
        File[] files = null;
        if (file.isDirectory()) {
            files = file.listFiles();
        }
        if (files != null) {
            final int[] count = {0};
            for (File file1 : files) {
                final File f = file1;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (MediaFileInfo msg : SearchFileUtil.searchFilePath(f, strExt)) {
                            info.add(msg);
                        }
                        count[0]++;
                    }
                }).start();
            }
            Log.i("Wang00", "setSearch: count=" + count[0] + "length=" + files.length);
            while (count[0] != files.length) {
                Log.i("Wang", "searchLoop: count=" + count[0] + "length=" + files.length);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return info;
    }

    @Override
    protected void onPostExecute(List<MediaFileInfo> strings) {
        super.onPostExecute(strings);
        if (strings.size() > 0) {
            mCompletionListener.onCompletionSearch(strings);
        }
    }

    public interface OnSearchCompletionListener {
        void onCompletionSearch(List<MediaFileInfo> fileList);
    }
}