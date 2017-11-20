package com.xyxl.tianyingn3.logs;

import com.orhanobut.logger.Logger;

/**
 * Created by rocgoo on 2017/11/9 下午1:45.
 * Function：
 */

public class LogUtil {

    //log打印开关
    private static final boolean needLog = true;
    private static final boolean needErrorLog = true;

    public static void e(String con)
    {
        if(needLog || needErrorLog)
        {
            Logger.e(con);
        }
    }
    public static void i(String con)
    {
        if(needLog)
        {
            Logger.i(con);
        }
    }
    public static void d(String con)
    {
        if(needLog)
        {
            Logger.d(con);
        }
    }
    public static void w(String con)
    {
        if(needLog)
        {
            Logger.w(con);
        }
    }
}
