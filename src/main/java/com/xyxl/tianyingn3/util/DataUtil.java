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

    /**
     * Bd num 2 bytes str string.
     *
     * @param num the num
     * @return the string
     */
    public static String BdNum2BytesStr(String num)
    {
        long numLong = CommonUtil.Str2long(num);
        if(numLong > 0)
        {
            byte[] numBytes = new byte[3];
            numBytes[0] = (byte) (numLong/256/256);
            numBytes[1] = (byte) ((numLong/256)%256);
            numBytes[2] = (byte) (numLong%256);
            String res = byte2HexStr(numBytes,0);
            while(res.length()<6)
            {
                res = "0"+res;
            }
            return res;
        }
        else
        {
            return null;
        }
    }

    /**
     * Phone num 2 bytes str string.
     *
     * @param num the num
     * @return the string
     */
    public static String PhoneNum2BytesStr(String num)
    {
        long numLong = CommonUtil.Str2long(num);
        if(numLong > 0)
        {
            byte[] numBytes = new byte[5];

            numBytes[0] = (byte) ((numLong >> 32) & 0xFF);
            numBytes[1] = (byte) ((numLong >> 24) & 0xFF);
            numBytes[2] = (byte) ((numLong >> 16) & 0xFF);
            numBytes[3] = (byte) ((numLong >> 8) & 0xFF);
            numBytes[4] = (byte) ((numLong) & 0xFF);
            String res = byte2HexStr(numBytes,0);
            while(res.length()<10)
            {
                res = "0"+res;
            }
            return res;
        }
        else
        {
            return null;
        }
    }

    /**
     * Is phone num boolean.
     *
     * @param num the num
     * @return the boolean
     */
    public static boolean isPhoneNum(String num)
    {
        if(num.length() == 11 && num.startsWith("1"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isBdNum(String num)
    {
        if(num.length() >0 && num.length()<8 && CommonUtil.Str2int(num) != 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
