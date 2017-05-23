package com.tsugun.tsugunvideo.util;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * 显示字符串的转换
 * Created by shize on 2017/3/1.
 */

public class ConverterUtil {

    /**
     * 精确时间显示位置
     */
    public static final int ACCURATE_TO_SECOND = 0x10; // 精确到秒
    public static final int ACCURATE_TO_MINUTE = 0x20; // 精确到分
    public static final int ACCURATE_TO_HOUR = 0x30; // 精确到小时

    private static final float BYTE = 1024;

    /**
     * 从dp转换为px
     *
     * @param context  上下文
     * @param dipValue dp值
     * @return px值
     */
    public static float dipToPx(Context context, float dipValue) {
        // 获取比例
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    /**
     * 从px转换为dp
     *
     * @param context 上下文
     * @param pxValue px值
     * @return dp值
     */
    public static float pxToDip(Context context, float pxValue) {
        // 获取比例
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    /**
     * 将size转为符合的字符串表示出来
     *
     * @param size 大小
     * @return 合适的字符串
     */
    public static String getConvertedSize(double size) {
        DecimalFormat df = new DecimalFormat(".00");
        int base = (int) (Math.log(size) / Math.log(BYTE));
        for (int i = 0; i < base; i++) {
            size /= BYTE;
        }
        switch (base) {
            case 1:
                return df.format(size) + "KB";
            case 2:
                return df.format(size) + "MB";
            case 3:
                return df.format(size) + "GB";
            default:
                return df.format(size) + "Byte";
        }
    }

    /**
     * 将毫秒转换为显示时间字符串
     *
     * @param timeValue   总时间
     * @param displayMode One of {@link #ACCURATE_TO_SECOND},{@link #ACCURATE_TO_MINUTE},
     *                    {@link #ACCURATE_TO_HOUR}
     * @return 显示时间字符串
     */
    public static String getConvertedTime(long timeValue, int displayMode) {
        // 将毫秒转化为秒
        final int durationS = (int) (timeValue / 1000);
        switch (displayMode) {
            case ACCURATE_TO_MINUTE:
                return getTimeString(durationS / 60) + ":" + getTimeString(durationS % 60);
            case ACCURATE_TO_HOUR:
                return getTimeString(durationS / 3600) + ":" + getTimeString((durationS / 60) % 60)
                        + ":" + getTimeString(durationS % 60);
            default:
                return getTimeString(durationS);
        }
    }

    /**
     * 将时间转化为字符串
     *
     * @param time 时间
     * @return String
     */
    private static String getTimeString(int time) {
        return time < 10 ? "0" + time : String.valueOf(time);
    }

    /**
     * 获取存放该地址的文件夹
     *
     * @param url 地址
     * @return 文件夹地址
     */
    public static String getFolderFromUrl(String url) {
        return url.substring(0, url.lastIndexOf('/'));
    }
}
