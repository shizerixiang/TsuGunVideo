package com.tsugun.tsugunvideo.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tsugun.tsugunvideo.R;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity {

    private static final int START_ACTIVITY = 0x1;

    /**
     * 启动一个activity
     */
    private Handler handler = new TGHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 在初始化界面两秒后发送启动activity消息
        handler.sendEmptyMessageDelayed(START_ACTIVITY, 2000);
    }

    /**
     * 重写handler,防止内存泄露
     */
    private static class TGHandler extends Handler {
        // 用弱引用,可以保证在handler未关闭状态下关闭activity
        private WeakReference<SplashActivity> activityWeakReference;

        TGHandler(SplashActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = activityWeakReference.get();
            if (activity != null && msg.what == START_ACTIVITY) {
                // 使用activity过渡动画，有三种Explode从两边进入
                // Slide从底部进入，fade透明消失和闪现
                // 需要注意的是必须在style主题中设置android:windowContentTransitions属性为true
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("transition", "slide");
                activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                        activity).toBundle());
                activity.finish();
            }
        }
    }
}
