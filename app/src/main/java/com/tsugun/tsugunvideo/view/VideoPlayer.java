package com.tsugun.tsugunvideo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tsugun.tsugunvideo.R;
import com.tsugun.tsugunvideo.dao.MediaFileFinder;
import com.tsugun.tsugunvideo.db.BaseDBHelper;
import com.tsugun.tsugunvideo.entity.MediaFileInfo;
import com.tsugun.tsugunvideo.util.ConverterUtil;
import com.tsugun.tsugunvideo.util.SettingsUtil;

import java.lang.ref.WeakReference;

/**
 * 自定义视频播放器
 * Created by shize on 2017/3/11.
 */

public class VideoPlayer extends FrameLayout {

    private Context mContext;
    // handler更新标识
    private static final int PROGRESS_BAR_UPDATE = 0x01; // 更新进度
    // 触摸方向标识
    private static final int TOUCH_DIRECTION_HORIZONTAL = 0x100; // 水平滑动,控制进度
    private static final int TOUCH_DIRECTION_VERTICAL_LEFT = 0x200; // 垂直左侧滑动,控制亮度
    private static final int TOUCH_DIRECTION_VERTICAL_RIGHT = 0x201; // 垂直右侧滑动,控制音量
    private static final int PROGRESS_SEEK_TO_TIME = 5000; // 滑动快进变化值
    private static final int PROGRESS_SEEK_TO_BRIGHT = 25; // 滑动亮度变化值
    private static final int MAX_BRIGHTNESS = 255; // 最大系统亮度
    private static final String DB_TUSGUN = "tsugun"; //

    private boolean isProgressTouch; // 是否正在触摸进度条
    private boolean isStopSendMsg; // 是否停止发送消息
    private boolean isMoveProgress; // 正在移动进度
    private boolean isVideoPlay; // 视频播放状态
    private int mBrightness; // 系统亮度
    private int mCurrent; // 播放进度
    private Button mPlayBtn;// 播放按钮
    private Button mFullBtn;// 全屏按钮
    private Button mBackBtn; // 返回按钮
    private Button mOptionBtn; // 设置按钮
    private TextView mDurationTv; // 总播放时间
    private TextView mCurrentTv; // 当前播放时间
    private TextView mVideoTitleTv; // 播放视频名称
    private SeekBar mTimeProgress;// 播放进度条
    private SeekBar mVoiceProgress; // 音量进度条
    private SeekBar mBrightProgress; // 亮度进度条
    private RelativeLayout mVideoController; // 播放控件条
    private RelativeLayout mVideoTitle; // 播放标题信息条
    private LinearLayout mVoiceLayout; // 音量进度布局
    private LinearLayout mBrightLayout; // 亮度进度布局
    private TsuGunVideo mTsuGunVideo;// 自定义小屏视频
    public VideoPlayer.VideoUIHandler mVideoUIHandler; // 自定义UIHandler
    private AudioManager mAudioManager; // 音频管理器
    private Drawable mBarThumb; // 拖动按钮图片
    private MediaFileInfo mMediaFileInfo; // 视频文件信息
    private MediaFileFinder mInfoFinder; // 数据获取器

    private OnVideoPlayerControlClickListener mOnControlClickListener;

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setContent(attrs);
        initView();
        initData();
        initUI();
    }

    /**
     * 设置文件路径
     */
    public void setVideoUrl(String url) {
        mMediaFileInfo = mInfoFinder.getFileInfoByUrl(url);
        mTsuGunVideo.setVideoPath(url);
    }

    /**
     * 开始播放
     */
    public void playVideo() {
        onChangePlayState();
    }

    /**
     * 给视频控件设置点击事件
     */
    public void setOnControlClickListener(OnVideoPlayerControlClickListener onControlClickListener) {
        mOnControlClickListener = onControlClickListener;
        setButtonClickListener();
    }

    /**
     * 关闭销毁视频
     */
    public void stopVideo() {
        if (mTsuGunVideo != null) {
            mTsuGunVideo.stopPlayback();
        }
    }

    /**
     * 设置全屏按钮背景
     */
    public void setFullBtnBackground(int id) {
        mFullBtn.setBackgroundResource(id);
    }

    /**
     * 设置控件内容
     */
    private void setContent(AttributeSet attrs) {
        LayoutInflater.from(mContext).inflate(R.layout.custom_video_player, this, true);
        TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.VideoPlayer);
        if (attributes != null) {
            // 设置视频数据源
            String url = attributes.getString(R.styleable.VideoPlayer_video_url);
            if (url != null) {
                setVideoUrl(url);
            }
            attributes.recycle();
        }
    }

    private void initView() {
        mTsuGunVideo = (TsuGunVideo) findViewById(R.id.id_main_video_player);
        mTimeProgress = (SeekBar) findViewById(R.id.id_main_bar_time);
        mVoiceProgress = (SeekBar) findViewById(R.id.id_main_bar_voice);
        mBrightProgress = (SeekBar) findViewById(R.id.id_main_bar_bright);
        mPlayBtn = (Button) findViewById(R.id.id_main_btn_play);
        mFullBtn = (Button) findViewById(R.id.id_main_btn_full);
        mBackBtn = (Button) findViewById(R.id.id_main_video_title_back);
        mOptionBtn = (Button) findViewById(R.id.id_main_video_title_setting);
        mDurationTv = (TextView) findViewById(R.id.id_main_duration);
        mCurrentTv = (TextView) findViewById(R.id.id_main_current);
        mVideoTitleTv = (TextView) findViewById(R.id.id_main_video_title_name);
        mVideoController = (RelativeLayout) findViewById(R.id.id_main_video_control);
        mVideoTitle = (RelativeLayout) findViewById(R.id.id_main_video_title);
        mVoiceLayout = (LinearLayout) findViewById(R.id.id_main_layout_voice);
        mBrightLayout = (LinearLayout) findViewById(R.id.id_main_layout_bright);
    }

    private void initData() {
        isProgressTouch = false;
        isStopSendMsg = false;
        mInfoFinder = new BaseDBHelper(mContext, DB_TUSGUN, 1);
        mVideoUIHandler = new VideoPlayer.VideoUIHandler(this);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mBrightness = SettingsUtil.getSystemBrightness(mContext);
        mBarThumb = mTimeProgress.getThumb();
    }

    // 播放完成监听事件
    private MediaPlayer.OnCompletionListener mVideoCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayBtn.setBackgroundResource(R.drawable.ic_vector_control_play);
                }
            };
    // 播放窗口触摸事件
    private View.OnTouchListener mVideoTouchListener = new View.OnTouchListener() {
        private int mEffectiveTouch = 30; // 有效触摸设置距离
        private float mTouchHeight; // 初始高度
        private float mTouchWidth; // 初始宽度
        private boolean isCheckedSetting = false; // 是否已经检测到设置项
        private int mTouchSetting; // 触发修改的功能
        private boolean isAddValue = false; // 是否为增大音量
        private float mMoveX;
        private float mMoveY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchWidth = event.getX();
                    mTouchHeight = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mMoveX = event.getX() - mTouchWidth;
                    mMoveY = event.getY() - mTouchHeight;
                    setOptionValue(event);
                    break;
                case MotionEvent.ACTION_UP:
                    setVisibilityState();
                    isCheckedSetting = false;
                    isMoveProgress = false;
                    break;
            }
            return true;
        }

        /**
         * 设置操作
         */
        private void setOptionValue(MotionEvent event) {
            if (!isCheckedSetting) {
                mTouchSetting = checkTouchOrientation(event);
                // 已经判定设置项
                isCheckedSetting = true;
            } else {
                // 判断是否为有效滑动
                if (Math.abs(mMoveX) > mEffectiveTouch || Math.abs(mMoveY) > mEffectiveTouch) {
                    setValue();
                    // 为下次判断重新赋值位置
                    mTouchWidth = event.getX();
                    mTouchHeight = event.getY();
                }
            }
        }

        /**
         * 设置值
         */
        private void setValue() {
            switch (mTouchSetting) {
                case TOUCH_DIRECTION_HORIZONTAL:
                    // 设置进度
                    isAddValue = mMoveX > 0;
                    setVideoProgress();
                    break;
                case TOUCH_DIRECTION_VERTICAL_LEFT:
                    // 设置亮度
                    isAddValue = mMoveY <= 0;
                    setBright();
                    break;
                case TOUCH_DIRECTION_VERTICAL_RIGHT:
                    // 设置音量
                    isAddValue = mMoveY <= 0;
                    setAudio();
                    break;
            }
        }

        /**
         * 设置亮度
         */
        private void setBright() {
            if (mBrightness < 25) {
                mBrightness = 25;
            } else if (mBrightness > 230) {
                mBrightness = 230;
            }
            if (isAddValue) {
                // 增加亮度
                SettingsUtil.setSystemBrightness(mContext,
                        mBrightness += PROGRESS_SEEK_TO_BRIGHT);
            } else {
                // 降低亮度
                SettingsUtil.setSystemBrightness(mContext,
                        mBrightness -= PROGRESS_SEEK_TO_BRIGHT);
            }
            mBrightLayout.setVisibility(View.VISIBLE);
            mBrightProgress.setProgress(mBrightness);
        }

        /**
         * 设置进度
         */
        private void setVideoProgress() {
            isMoveProgress = true;
            if (isAddValue) {
                // 前进
                mTimeProgress.setProgress(mTimeProgress.getProgress() + PROGRESS_SEEK_TO_TIME);
            } else {
                // 后退
                mTimeProgress.setProgress(mTimeProgress.getProgress() - PROGRESS_SEEK_TO_TIME);
            }
        }

        /**
         * 设置音量
         */
        private void setAudio() {
            if (isAddValue) {
                // 加音量
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, 0);
            } else {
                // 减音量
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, 0);
            }
            mVoiceLayout.setVisibility(View.VISIBLE);
            mVoiceProgress.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }

        /**
         * 检测滑动方向
         */
        private int checkTouchOrientation(MotionEvent event) {
            if (Math.abs(mMoveX) > Math.abs(mMoveY)) {
                // 判断为横向滑动
                return TOUCH_DIRECTION_HORIZONTAL;

            } else {
                // 判断为纵向滑动
                int width = getMeasuredWidth();
                if (event.getX() < width / 2) {
                    // 判断为左侧纵向滑动,控制亮度
                    return TOUCH_DIRECTION_VERTICAL_LEFT;
                } else {
                    // 判断为右侧纵向滑动,控制声音
                    return TOUCH_DIRECTION_VERTICAL_RIGHT;
                }

            }
        }

        /**
         * 控件条的显示状态
         */
        private void setVisibilityState() {
            if (!isCheckedSetting) {
                // 只是在视频上点击
                if (mVideoController.getVisibility() == View.GONE) {
                    mTimeProgress.setThumb(mBarThumb);
                    mVideoController.setVisibility(View.VISIBLE);
                    mVideoController.startLayoutAnimation();
                    mVideoTitle.setVisibility(View.VISIBLE);
                    mVideoTitle.startLayoutAnimation();
                } else {
                    mTimeProgress.setThumb(null);
                    mVideoController.setVisibility(View.GONE);
                    mVideoTitle.setVisibility(View.GONE);
                }
            } else {
                // 在视频上点击并滑动了
                if (mVoiceLayout.getVisibility() == View.VISIBLE) {
                    mVoiceLayout.setVisibility(View.GONE);
                }
                if (mBrightLayout.getVisibility() == View.VISIBLE) {
                    mBrightLayout.setVisibility(View.GONE);
                }
            }
        }
    };
    // 改变进度条监听事件
    private SeekBar.OnSeekBarChangeListener mTimeChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                private int mProgress = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (isMoveProgress) {
                        mTsuGunVideo.seekTo(progress);
                    } else {
                        mProgress = progress;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (mMediaFileInfo != null) {
                        isProgressTouch = true;
                        if (mTsuGunVideo.isPlaying()) {
                            mPlayBtn.setBackgroundResource(R.drawable.ic_vector_control_play);
                            mTsuGunVideo.pause();
                        }
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mMediaFileInfo != null) {
                        isProgressTouch = false;
                        mTsuGunVideo.seekTo(mProgress);
                        mPlayBtn.setBackgroundResource(R.drawable.ic_vector_control_pause);
                        mTsuGunVideo.start();
                        if (isStopSendMsg) {
                            mVideoUIHandler.sendEmptyMessage(PROGRESS_BAR_UPDATE);
                        }
                    }
                }
            };

    // 拦截触摸事件
    private View.OnTouchListener mIntercept = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };

    /**
     * 初始化UI界面
     */
    private void initUI() {
        mTsuGunVideo.setOnCompletionListener(mVideoCompletionListener);
        mTsuGunVideo.setOnTouchListener(mVideoTouchListener);
        mVideoController.setOnTouchListener(mIntercept);
        mVideoTitle.setOnTouchListener(mIntercept);
        mTimeProgress.setMax(mTsuGunVideo.getDuration());
        mTimeProgress.setOnSeekBarChangeListener(mTimeChangeListener);
        mVoiceProgress.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mVoiceProgress.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mBrightProgress.setMax(MAX_BRIGHTNESS);
        mBrightProgress.setProgress(mBrightness);
        mVideoUIHandler.sendEmptyMessage(PROGRESS_BAR_UPDATE);
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaFileInfo != null) {
                    onChangePlayState();
                }
            }
        });
        setPlayVideoPath();
    }

    /**
     * 设置点击事件
     */
    private void setButtonClickListener() {
        if (mOnControlClickListener != null) {
            mFullBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnControlClickListener.onFullScreenClick(v);
                }
            });
            mOptionBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnControlClickListener.onOptionClick(v);
                }
            });
            mBackBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnControlClickListener.onBackClick(v);
                }
            });
        }
    }

    /**
     * 设置视频源
     */
    private void setPlayVideoPath() {
        if (mMediaFileInfo != null) {
            // 录制视频处理方向的问题
//            mTsuGunVideo.setRotation(mVideoPathList.get(mPathPosition).getRotation());
            mTsuGunVideo.setVideoPath(mMediaFileInfo.getUrl());
        }
    }

    /**
     * 改变播放状态
     */
    private void onChangePlayState() {
        if (!mTsuGunVideo.isPlaying()) {
            mPlayBtn.setBackgroundResource(R.drawable.ic_vector_control_pause);
            mTsuGunVideo.start();
        } else {
            mPlayBtn.setBackgroundResource(R.drawable.ic_vector_control_play);
            mTsuGunVideo.pause();
        }
    }

    /**
     * 重写Handler,防止内存泄露
     */
    private static class VideoUIHandler extends Handler {
        // 弱引用activity
        private WeakReference<VideoPlayer> mVideoPlayer;

        VideoUIHandler(VideoPlayer player) {
            mVideoPlayer = new WeakReference<>(player);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoPlayer player = mVideoPlayer.get();
            if (player != null) {
                switch (msg.what) {
                    case PROGRESS_BAR_UPDATE:
                        if (!player.isProgressTouch) {
                            int duration = player.mTsuGunVideo.getDuration();
                            int current = player.mTsuGunVideo.getCurrentPosition();
                            player.isStopSendMsg = false;
                            // 获取总进度
                            player.mTimeProgress.setMax(duration);
                            player.mDurationTv.setText(ConverterUtil.getConvertedTime(duration, ConverterUtil.ACCURATE_TO_HOUR));
                            // 获取当前进度
                            player.mTimeProgress.setProgress(current);
                            player.mCurrentTv.setText(ConverterUtil.getConvertedTime(current, ConverterUtil.ACCURATE_TO_HOUR));
                            player.mVideoTitleTv.setText(player.mMediaFileInfo.getTitle());
                            player.mVideoUIHandler.sendEmptyMessageDelayed(
                                    PROGRESS_BAR_UPDATE, 240);
                        } else {
                            player.isStopSendMsg = true;
                        }
                        break;
                }
            }
        }
    }

    /**
     * 视频播放器控件点击事件接口
     */
    public interface OnVideoPlayerControlClickListener {

        /**
         * 给全屏按钮设置点击事件
         *
         * @param v 全屏按钮
         */
        void onFullScreenClick(View v);

        /**
         * 给设置按钮设置点击事件
         *
         * @param v 设置按钮
         */
        void onOptionClick(View v);

        /**
         * 给返回按钮设置点击事件
         *
         * @param v 返回按钮
         */
        void onBackClick(View v);
    }
}
