package com.tsugun.tsugunvideo.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.tsugun.tsugunvideo.R;
import com.tsugun.tsugunvideo.adapter.VideoListAdapter;
import com.tsugun.tsugunvideo.dao.MediaFileFinder;
import com.tsugun.tsugunvideo.db.BaseDBHelper;
import com.tsugun.tsugunvideo.entity.MediaFileInfo;
import com.tsugun.tsugunvideo.service.SearchFileTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // handler更新标识
    public static final int VIDEO_PATH_LIST_UPDATE = 0x02; // 更新列表
    // 触摸方向标识
    public static final String DB_TUSGUN = "tsugun"; //

    private boolean isFirst; // 是否第一次
    private List<MediaFileInfo> mVideoPathList; // 视频文件路径集合
    private SearchFileTask mSearchFileTask; // 扫描文件子线程
    private MediaFileFinder mMediaFileFinder; // 数据库助手
    private ProgressBar mLoadingProgress; // 加载进度
    private RecyclerView mVideoPathListView; // 视频路径列表
    private Toolbar mToolbar; // 标题栏
    public static VideoUIHandler mVideoUIHandler; // 自定义UIHandler
    private VideoListAdapter mVideoAdapter; // 自定义路径列表适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTransition();

        initView();
        initData();
        initUI();
    }

    /**
     * 设置过渡动画
     */
    private void setTransition() {
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(280);
        getWindow().setEnterTransition(slide);
    }

    /**
     * 初始化view
     */
    private void initView() {
        mVideoPathListView = (RecyclerView) findViewById(R.id.id_main_list_video_path);
        mToolbar = (Toolbar) findViewById(R.id.id_video_toolbar);
        mLoadingProgress = (ProgressBar) findViewById(R.id.id_video_loading);
    }

    /**
     * 初始化data
     */
    private void initData() {
        setSupportActionBar(mToolbar);
        isFirst = true;
        mMediaFileFinder = new BaseDBHelper(this, DB_TUSGUN, 1);
        mVideoUIHandler = new VideoUIHandler(this);
        mVideoPathList = new ArrayList<>();
        mSearchFileTask = new SearchFileTask(mMediaFileFinder);
        mVideoPathList = mMediaFileFinder.getAllFileInfo();
        mVideoAdapter = new VideoListAdapter(MainActivity.this, mVideoPathList);
    }

    // 路径列表子项点击事件
    private VideoListAdapter.OnVideoItemClickListener mVideoPathItemListener =
            new VideoListAdapter.OnVideoItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent intent = new Intent(MainActivity.this, InformationActivity.class);
                    intent.putExtra(VideoActivity.INTENT_VIDEO_MESSAGE, mVideoPathList.get(
                            position).getUrl());
                    Pair imagePair = new Pair<>(v.findViewById(R.id.id_main_video_image),
                            getString(R.string.str_transition_thumb));
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(MainActivity.this, imagePair);
                    startActivity(intent, activityOptions.toBundle());
                }

                @Override
                public void onItemLongClick(View v, int position) {

                }
            };
    // 搜索文件完成事件
    private SearchFileTask.OnSearchCompletionListener mCompletionSearchListener =
            new SearchFileTask.OnSearchCompletionListener() {
                @Override
                public void onCompletionSearch(List<MediaFileInfo> fileList) {
                    mVideoPathList = fileList;
                    mLoadingProgress.setVisibility(View.GONE);
                    addListData();
                }
            };

    /**
     * 初始化UI界面
     */
    private void initUI() {
        mVideoAdapter.setOnVideoItemClickListener(mVideoPathItemListener);
        mVideoPathListView.setAdapter(mVideoAdapter);
        // 添加布局管理器
        LinearLayoutManager linearLayout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mVideoPathListView.setLayoutManager(linearLayout);
        mVideoPathListView.setItemAnimator(new DefaultItemAnimator());
        mSearchFileTask.setCompletionListener(mCompletionSearchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video_list_title, menu);
        return true;
    }

    /**
     * 悬浮按钮点击事件
     */
    public void onSearchButtonClick(View view) {

    }

    /**
     * 搜索菜单按钮点击事件注册
     */
    public void onSearchVideoFileClick(MenuItem item) {
        if (isFirst) {
            mSearchFileTask.execute();
            mLoadingProgress.setVisibility(View.VISIBLE);
            isFirst = false;
        } else {
            mVideoPathList = mMediaFileFinder.getAllFileInfo();
            addListData();
        }
    }

    /**
     * 添加列表数据
     */
    private void addListData() {
        mVideoAdapter.clearVideoItems();
        for (int i = 0; i < mVideoPathList.size(); i++) {
            mVideoAdapter.addVideoItem(i, mVideoPathList.get(i));
        }
    }

    /**
     * 清空菜单按钮点击事件注册
     */
    public void onClearVideoListClick(MenuItem item) {
        mVideoAdapter.clearVideoItems();
        if (isFirst) {
            mMediaFileFinder.deleteAllFileInfo();
        }
    }

    /**
     * 重写Handler,防止内存泄露
     */
    public static class VideoUIHandler extends Handler {
        // 弱引用activity
        private WeakReference<MainActivity> activityWeakReference;

        VideoUIHandler(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case VIDEO_PATH_LIST_UPDATE:
                        if (activity.mVideoAdapter != null) {
                            activity.mVideoAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        }
    }


}
