package com.tsugun.tsugunvideo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * 自定义播放器
 * Created by shize on 2017/2/28.
 */

public class TsuGunVideo extends VideoView {

    private int defaultWidth = 960;
    private int defaultHeight = 540;

    public TsuGunVideo(Context context) {
        super(context);
    }

    public TsuGunVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TsuGunVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(defaultWidth, widthMeasureSpec);
        int height = getDefaultSize(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
