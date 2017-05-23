package com.tsugun.tsugunvideo.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsugun.tsugunvideo.R;
import com.tsugun.tsugunvideo.dao.MediaFileFinder;
import com.tsugun.tsugunvideo.db.BaseDBHelper;
import com.tsugun.tsugunvideo.entity.MediaFileInfo;
import com.tsugun.tsugunvideo.util.CaptureVideoBitmapUtil;
import com.tsugun.tsugunvideo.util.ConverterUtil;
import com.tsugun.tsugunvideo.view.VideoPlayer;

public class InformationActivity extends AppCompatActivity
        implements VideoPlayer.OnVideoPlayerControlClickListener {

    private boolean isLandScape;
    private TextView mNameTv;
    private TextView mRecordTv;
    private TextView mDurationMsgTv;
    private TextView mSizeTv;
    private TextView mRatioTv;
    private TextView mPathTv;
    private ImageView mTitleIv;
    private CollapsingToolbarLayout mToolbarLayout;
    private MediaFileInfo mMediaInfo;
    private VideoPlayer mVideoPlayer;
    private View mVideoMargin;
    private LinearLayout mVideoGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        setTransition();

        initView();
        initData();
        initUI();
    }

    /**
     * 设置过渡动画
     */
    private void setTransition() {
        Slide slide = new Slide(Gravity.TOP);
        slide.setDuration(280);
        getWindow().setEnterTransition(slide);
    }

    private void initView() {
        mNameTv = (TextView) findViewById(R.id.id_info_name_content);
        mRecordTv = (TextView) findViewById(R.id.id_info_record_content);
        mDurationMsgTv = (TextView) findViewById(R.id.id_info_duration_content);
        mSizeTv = (TextView) findViewById(R.id.id_info_size_content);
        mRatioTv = (TextView) findViewById(R.id.id_info_ratio_content);
        mPathTv = (TextView) findViewById(R.id.id_info_path_content);
        mTitleIv = (ImageView) findViewById(R.id.id_info_title_image);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.id_info_toolbar_layout);
        mVideoPlayer = (VideoPlayer) findViewById(R.id.id_video_player);
        mVideoMargin = findViewById(R.id.id_video_margin);
        mVideoGroup = (LinearLayout) findViewById(R.id.id_video_group);
    }

    private void initData() {
        MediaFileFinder finder = new BaseDBHelper(this, MainActivity.DB_TUSGUN, 1);
        String url = getIntent().getStringExtra(VideoActivity.INTENT_VIDEO_MESSAGE);
        mMediaInfo = finder.getFileInfoByUrl(url);
        isLandScape = false;
    }

    private void initUI() {
        mNameTv.setText(mMediaInfo.getTitle());
        mRecordTv.setText(ConverterUtil.getConvertedTime((int) mMediaInfo.getRecord(), ConverterUtil.ACCURATE_TO_HOUR));
        mDurationMsgTv.setText(ConverterUtil.getConvertedTime((int) mMediaInfo.getDuration(), ConverterUtil.ACCURATE_TO_HOUR));
        mSizeTv.setText(ConverterUtil.getConvertedSize(mMediaInfo.getSize()));
        mRatioTv.setText((int) mMediaInfo.getWidth() + " X " + (int) mMediaInfo.getHeight());
        mPathTv.setText(mMediaInfo.getUrl());
        mToolbarLayout.setTitle(mMediaInfo.getTitle().substring(
                0, mMediaInfo.getTitle().indexOf('.')));
        mTitleIv.setImageBitmap(getThumbBitmap());
        mVideoPlayer.setVideoUrl(mMediaInfo.getUrl());
        mVideoPlayer.setOnControlClickListener(this);
    }

    @Override
    public void onFullScreenClick(View v) {
        changeScreenState();
    }

    /**
     * 修改屏幕状态
     */
    private void changeScreenState() {
        if (mMediaInfo != null) {
            isLandScape = !isLandScape;
            setVideoOrientation();
        }
    }

    @Override
    public void onOptionClick(View v) {

    }

    @Override
    public void onBackClick(View v) {
        if (isLandScape) {
            changeScreenState();
        } else {
            finish();
        }
    }

    /**
     * 获取缩略图
     */
    private Bitmap getThumbBitmap() {
        return CaptureVideoBitmapUtil.getVideoBitmap(this, mMediaInfo.getUrl(),
                360, 640, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    /**
     * 开始播放按钮点击事件注册
     */
    public void onStartVideoClick(View view) {
        if (mMediaInfo != null) {
            mVideoPlayer.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            mVideoPlayer.playVideo();
        } else {
            Snackbar.make(view, "视频文件不存在！", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * 修改屏幕方向
     */
    private void setVideoOrientation() {
        if (isLandScape) {
            // 设置为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            // 设置为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayer.stopVideo();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onChangeScreenState();
    }

    /**
     * 改变屏幕状态
     */
    private void onChangeScreenState() {
        // 判断屏幕方向
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            mVideoPlayer.setFullBtnBackground(R.drawable.ic_vector_control_not_screen);

            ViewGroup.LayoutParams layoutParams = mVideoGroup.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoGroup.setLayoutParams(layoutParams);
            mVideoMargin.setVisibility(View.VISIBLE);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            // 竖屏
            mVideoPlayer.setFullBtnBackground(R.drawable.ic_vector_control_full_screen);

            ViewGroup.LayoutParams layoutParams = mVideoGroup.getLayoutParams();
            // px和dp的相互转化
            layoutParams.height = (int) ConverterUtil.dipToPx(InformationActivity.this, 216);
            mVideoGroup.setLayoutParams(layoutParams);
            mVideoMargin.setVisibility(View.GONE);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
