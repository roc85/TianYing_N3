package com.xyxl.tianyingn3.util;

/**
 * Created by Administrator on 2017/11/13 15:35
 * Version : V1.0
 * Introductions : 公共方法类
 */

public class DataUtil {

    // 异或校验
    public static byte xor(byte[] x, int len)
    {
        byte y = 0;
        for (int idx = 0; idx < len; idx++)
        {
            y ^= x[idx];
        }

        return y;
    }

    /**
     * bytes字符串转换为Byte值
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src)
    {
        int m=0,n=0;
        int l=src.length()/2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m=i*2+1;
            n=m+1;
//            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
//            ret[i]=(byte)(Integer.decode("0x" + src.substring(i*2, m) + src.substring(m,n)));
            int intValue = Integer.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
            ret[i] = (byte) intValue;
        }
        return ret;
    }
    /**
     * bytes转换成十六进制字符串
     * @param  b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b,int type)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            if(type==1)
                sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
}
