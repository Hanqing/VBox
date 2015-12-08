package com.smart.vbox.support.utils;

import java.sql.Timestamp;

/**
 * @author lhq
 *         created at 2015/12/5 16:08
 */
public class TimeUtils {
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }


    public static String getTimestamp() {
        return new Timestamp(getCurrentTime()).toString();
    }
}
