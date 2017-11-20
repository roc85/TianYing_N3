package com.xyxl.tianyingn3.solutions;



/**
 * 各地图API坐标系统比较与转换;
 * WGS84坐标系：即地球坐标系，国际上通用的坐标系。设备一般包含GPS芯片或者北斗芯片获取的经纬度为WGS84地理坐标系,
 * 谷歌地图采用的是WGS84地理坐标系（中国范围除外）;
 * GCJ02坐标系：即火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。由WGS84坐标系经加密后的坐标系。
 * 谷歌中国地图和搜搜中国地图采用的是GCJ02地理坐标系; BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系;
 * 搜狗坐标系、图吧坐标系等，估计也是在GCJ02基础上加密而成的。 chenhua
 */
public class GpsData
{

    public static final String BAIDU_LBS_TYPE = "bd09ll";

    public static double pi = 3.1415926535897932384626;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    // 经纬度度分秒转换为小数
    public static double convertToDecimal(double du, double fen, double miao) {
        if (du < 0)
            return -(Math.abs(du) + (Math.abs(fen) + (Math.abs(miao) / 60)) / 60);
        return Math.abs(du) + (Math.abs(fen) + (Math.abs(miao) / 60)) / 60;
    }

    //小数转度分秒
    public static String convertTodfm(double num) {
        int du = (int) (num); // 获取整数部分
        double temp = (num - (int)num) * 60;
        int fen = (int)(temp);
        double temp1 = (temp - (int)temp) * 60;
//        int miao = (temp1 - (int)temp1 < 0.5)? (int)temp1:(int)temp1 + 1;
        //int Xiaomiao=(temp1-miao)<0.05?(int)temp1:(int)temp1 + 1;
//        int Xiaomiao=5;

        int miao =(int)temp1;
        int Xiaomiao=(int) ((temp1 - (int)temp1)*60);
        if (num < 0)
            return "-" + du + "x" + fen + "x" + miao + "x"+Xiaomiao;
        return du + "度" + fen + "分" + miao + "."+Xiaomiao+"秒";
    }

    /**
     * 84 to 火星坐标系 (GCJ-02) World Geodetic System ==> Mars Geodetic System
     *
     * @param lat
     * @param lon
     * @return
     */
    public static Gps gps84_To_Gcj02(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Gps(mgLat, mgLon);
    }

    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    public static Gps transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new Gps(lat, lon);
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Gps(mgLat, mgLon);
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    public static String formatRnssDisplay(String s)
    {
        String sDisplay="";
        String[] sTemp=s.split("m");
        if(sTemp.length>3)
        {
            sDisplay=sTemp[0]+"度"+sTemp[1]+"分"+sTemp[2]+"."+sTemp[3]+"秒";
        }
        else
        {
            sDisplay=convertTodfm(Double.valueOf(s));
        }

        return sDisplay;
    }

    /**
     * 转换为小数格式
     * @param s
     * @param type 暂时无用
     * @return
     */
    public static double formatSerialRnssPos(String s, int type)
    {
//        String sDisplay="";
        String[] temp=s.split("\\.");
        double d=0;
        if(temp[0].length()==4)
        {
            //纬度
            d=Double.valueOf(s.substring(0, 2));
            d+=Double.valueOf(s.substring(2))/60;
        }
        else if(temp[0].length()==5)
        {
            //纬度
            d=Double.valueOf(s.substring(0, 3));
            d+=Double.valueOf(s.substring(3))/60;
        }
//        sDisplay=(d+"");
        return d;
    }

    public static String formatBTRdssPos(String s)
    {
        String sDisplay="";
        String[] sTemp=s.split("度");
        String[] stemp0=sTemp[1].split("分");
        double d=0;
        d=Double.valueOf(sTemp[0])+Double.valueOf(stemp0[0])/60;
        sDisplay=convertTodfm(d);
        return sDisplay;
    }
}
