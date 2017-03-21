package com.tsugun.tsugunvideo.util;

import android.content.Context;

/**
 * dp和px间的相互转换
 * Created by shize on 2017/3/1.
 */

public class ConverterUtil {

    private static float KB = 1024;
    private static float MB = 1024*1024;
    private static float GB = 1024*1024*1024;

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
    public static String getConvertedSize(long size){
        String strSize;
        int kb, mb, gb;
        if (size < KB) {
            strSize = size+"B";
        } else if (size < MB){
            kb = (int) (size / KB);
            strSize = kb + "KB";
        } else if (size < GB){
            mb = (int) (size / MB);
            kb = (int) (((size % MB) / MB) * 10);
            strSize = mb + "." + kb + "MB";
        } else {
            gb = (int) (size / GB);
            mb = (int) (((size % GB) / GB) * 10);
            strSize = gb + "." + mb + "GB";
        }
        return strSize;
    }

    /**
     * 将毫秒转换为显示时间字符串
     *
     * @param time 总时间
     * @return 显示时间字符串
     */
    public static String getConvertedTime(int time) {
        int durationS = time / 1000;

        String strS;
        int sTime = durationS % 60;
        if (sTime < 10) {
            strS = "0" + sTime;
        } else {
            strS = String.valueOf(sTime);
        }

        String strM;
        int mTime = (durationS / 60) % 60;
        if (mTime < 10) {
            strM = "0" + mTime;
        } else {
            strM = String.valueOf(mTime);
        }

        String strH;
        int hTime = durationS / 3600 / 60;
        if (hTime < 10) {
            strH = "0" + hTime;
        } else {
            strH = String.valueOf(hTime);
        }

        return strH + ":" + strM + ":" + strS;
    }

}
