package com.tsugun.tsugunvideo.util;

import android.content.Context;
import android.provider.Settings;

/**
 * 系统设置工具类
 * Created by shize on 2017/3/1.
 */

public class SettingsUtil {

    private static final int MAX_BRIGHTNESS = 255; // 最大系统亮度

    /**
     * 获取系统亮度
     *
     * @param context 上下文
     * @return 亮度
     */
    public static int getSystemBrightness(Context context) {
        int systemBrightness = 25;
        try {
            Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 设置屏幕亮度
     *
     * @param context    上下文
     * @param brightness 亮度值
     */
    public static void setSystemBrightness(Context context, int brightness) {
        if (brightness < MAX_BRIGHTNESS && brightness > 0) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    brightness);
        } else if (brightness > 0) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    MAX_BRIGHTNESS);
        } else {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    0);
        }
    }

}
