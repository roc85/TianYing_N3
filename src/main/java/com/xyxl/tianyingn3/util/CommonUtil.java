package com.xyxl.tianyingn3.util;

import com.xyxl.tianyingn3.logs.LogUtil;

/**
 * Created by Administrator on 2017/11/15 11:05
 * Version : V1.0
 * Introductions :
 */

public class CommonUtil {

    /**
     * 字符串转int，无效时返回0
     *
     * @param src the src
     * @return the int
     */
    public static int Str2int(String src)
    {
        int res = 0;
        try
        {
            res = Integer.valueOf(src);
        }
        catch(Exception e)
        {
            LogUtil.e(e.toString());
        }
        return res;
    }

    /**
     * 字符串转double，无效时返回0
     *
     * @param src the src
     * @return the double
     */
    public static double Str2double(String src)
    {
        double res = 0;
        try
        {
            res = Double.valueOf(src);
        }
        catch(Exception e)
        {
//            LogUtil.e(e.toString());
        }
        return res;
    }
    /**
     * 字符串转double，无效时返回0
     *
     * @param src the src
     * @return the double
     */
    public static float Str2float(String src)
    {
        float res = 0;
        try
        {
            res = Float.valueOf(src);
        }
        catch(Exception e)
        {
//            LogUtil.e(e.toString());
        }
        return res;
    }
}
