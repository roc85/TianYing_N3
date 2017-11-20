package com.xyxl.tianyingn3.solutions;

import com.xyxl.tianyingn3.util.CommonUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/11/15 10:37
 * Version : V1.0
 * Introductions :
 */

public class BdSdk_v2_1 {
    public static  String[] frameStr=new String[256];

    /** 异或校验和 byte
     *
     */
    private static byte xor(byte[] x)
    {
        byte y = 0;
        int len = x.length;
        for (int idx = 1; idx < len-1; idx++)
        {
            y ^= x[idx];
        }

        return y;
    }
    /**
     * 异或校验和 String
     *
     */
    private static String xor(String Str) {
        char[] StrChars = Str.toCharArray();
        int x = 0x00;
        int c = 0x00;
        int d = 0x00;
        for (int i = 1; i < StrChars.length - 1; i++)
            x ^= StrChars[i];
        if(((x>>4)&0x0F) >= 0 && ((x>>4)&0x0F) <= 9)
            c = (byte) (((x>>4)&0x0F) + 0x30);
        else
            c = (byte) (((x>>4)&0x0F) + 0x37);
        if((x&0x0F) >= 0 && (x&0x0F) <= 9)
            d = (byte) ((x&0x0F) + 0x30);
        else
            d = (byte) ((x&0x0F) + 0x37);
        byte[] y = {(byte) c, (byte) d};
        return new String(y);
    }


    /**
     * 发送RMO指令------输入输出设置
     * @param Comm 需执行的指令
     * @param Mode 模式  1 关闭指定语句 2 打开指定语句 3 关闭全部语句 值为3和4时 指定语句为空
     * @param IntFre 频度
     * @return String Str =组包后报文
     * */
    public static String BD_SendRMO(String Comm, int Mode, int IntFre) {
        String Str;
        int count = 0;
        String SFre = Integer.toString(IntFre);//频度
        Str = "$CCRMO,";
        if(Mode == 1 || Mode ==2) {
            count = 3 - Comm.length();
            for(int i = 0; i < count; i++)
                Str += "0";
            Str += Comm;						//指令
            Str += ",";
            Str += Mode;						//模式 1关闭 2打开 3全部关闭
            Str += ",";
            Str += SFre;							//频度
            Str += "*";
            Str += xor(Str);						//校验和
            Str +="\r\n";
        }else {										//模式为非1，2时 指令为空
            Str += "," + Mode + ",*";
            Str += xor(Str);						//校验和
            Str +="\r\n";							//回车换行符
        }
        return Str;
    }


    /**
     * 发送BSS，波束设置
     * @param IXybs 响应波束号
     * @return String Str = 组包后报文
     * */
    public static String BD_SendBSS(int IXybs)  {
        String Str;
        Str = "$CCBSS,";
        if(IXybs < 10)						//响应波束号，不是10前面补0
            Str += "0" +IXybs;
        else
            Str += IXybs;
        Str += ",";
        Str += "00";			//时差波束号，不是10前面补0

        Str += "*";
        Str += xor(Str);						//校验和
        Str +="\r\n";							//回车换行符
        return Str;
    }


    /**
     * 发送CXA 信息查询申请
     * @param IType 查询类别 0=定位查询 1=通信查询
     * @param IMode 查询方式 当查询类别IType为0时：
     * 										1＝查询下属用户最近1 次定位信息；
     * 										2＝查询下属用户最近2 次定位信息；
     * 										3＝查询下属用户最近3次定位信息；
     * 										  当查询类别IType为1 时：
     *										1＝查询最新存入电文；
     *										2＝按发信地址查询；
     *										3＝回执查询。
     *@param SAdd 用户地址
     *										a）若查询类别为0，用户地址为被查询的下属用户地址；
     *										b）若查询类别为1，查询方式为1，用户地址为空；
     *										c）若查询类别为1，查询方式为2，用户地址为发信方地址；
     *										d）若查询类别为1，查询方式为3，用户地址为收信方地址。
     *@return String Str = 组包后报文
     * */
    public static String BD_SendCXA(int IType, int IMode, String SAdd) {
        String Str;
        int count = 0;
        Str = "$CCCXA,";
        Str = Str + IType + "," + IMode + ",";;	//查询类别和查询方式
        if(IType == 1 && IMode == 1)				//同时为1时，用户地址为空
            Str += "*";
        else {
            if(SAdd.length() < 7) {						//地址不满7位前面补0
                count = 7 - SAdd.length();
                for(int i = 0; i< count; i++)
                    Str += "0";
            }
            Str += SAdd;
            Str += "*";
        }
        Str += xor(Str);										//校验和
        Str +="\r\n";											//回车换行符
        return Str;
    }
    /**
     * 发送DSA 设置用户设备发送定时申请
     * @param SAdd 用户地址
     * @param ITiming 定时方式 1=单相定时申请 2=双向定时申请
     * @param BIns 有无位置信息指示 true=A（有概略位置） false = V（无概略位置）
     * @param Latitude 纬度
     * @param Longitude 经度
     * @param Freq 申请频度
     * @param Value 单向零值 定时方式为2时填0
     * @param FValue 附加零值
     * @return String Str = 组包后报文
     * */
    public static String BD_SendDSA(String SAdd, int ITiming, boolean BIns, double Latitude, double Longitude
            ,int Freq, int Value, int FValue) {
        String Str;
        Str = "$CCDSA,";
        int count = 0;
        if(SAdd.length() < 7) {						//地址不满7位前面补0
            count = 7 - SAdd.length();
            for(int i = 0; i< count; i++)
                Str += "0";
        }
        Str += SAdd;
        Str += ",";
        Str += ITiming;
        Str += ",";
        if(BIns) {
            Str += "A";
            Str += ",";
            Str += Latitude;
            Str += ",";
            Str += Longitude;
        }
        else
            Str += "V";
        Str += ",";
        Str += Freq;
        Str += ",";
        if(ITiming == 1)	//双向定时申请时，零值为0
            Str += "0";
        else
            Str += Value;
        Str += ",";
        Str += FValue;
        Str += "*";
        Str += xor(Str);										//校验和
        Str +="\r\n";											//回车换行符
        return Str;
    }

    /**
     * 发送DWA 定位申请
     * @param SAdd 用户地址
     * @param EmergencyLoc 是否是紧急定位	true=紧急定位 false=普通定位
     * @param Altimetry 测高方式 0=有高程   1=无测高   2=测高1   3=测高2
     * @param Indicator 高程指示 true=H（高空） false=L（普通）
     * @param Altdata 高程值
     * @param Antenna 天线高
     * @param Apdata 气压数据
     * @param Temperature 温度数据
     * @param Freq 频度
     * @return String Str  = 组包后报文
     * */
    public static String BD_SendDWA(String SAdd, boolean EmergencyLoc, int Altimetry, boolean Indicator,
                                    int Altdata, int Antenna,  int Apdata, int Temperature, int Freq) {
        String Str;
        int count = 0;
        Str = "$CCDWA,";
        if(SAdd.length() < 7){					//用户地址，不满7位前面补0
            count = 7 - SAdd.length();
            for(int i = 0; i< count; i ++)
                Str += "0";
        }
        Str += SAdd;
        Str += ",";
        if (EmergencyLoc == true)			//是否紧急定位 A紧急定位  V普通定位
            Str += "A,";
        else
            Str += "V,";
        Str += Altimetry;							//测高方式
        if(Indicator == true)						//高程指示 H高空 L普通
            Str += ",H,";
        else
            Str += ",L,";
        if(Altimetry == 0) {						//测高为0时，高程数据为天线高程数据 其他数据为空
            Str += Antenna + ",";
            Str += Antenna + ",,,";
        }
        else if(Altimetry == 1) {					//测高为1时，高程数据、气压、温度为空，天线高数据有
            Str =Str + "," + Antenna + ",,,";
        }
        else if(Altimetry == 2) {					//测高为2时，高程数据为空，其他数据有
            Str =Str + "," + Antenna + "," + Apdata +"," + Temperature + ",";
        }
        else												//测高为3时，所有数据都有
            Str = Str +Altdata + "," + Antenna + "," + Apdata +"," + Temperature + ",";
        String Fre = Integer.toString(Freq);
        int len = 0;
        if(Fre.length() < 3){						//频度，不足3位前面补0
            len = 3 - Fre.length();
            for(int i = 0 ; i< len; i++)
                Str += "0";
        }
        Str += Freq;
        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 发送TXA 通信申请
     * @param SAdd 收信地址
     * @param Mode 通信方式 0=特快通信 1=普通通信
     * @param Type  报文传输方式 0=汉字 1=代码 2=混发
     * @param Content 报文内容
     * @return String Str = 组包后报文
     * */
    public static String BD_SendTXA(String SAdd, int Mode, int Type, String Content) {
        String Str;
        int count = 0;
        Str = "$CCTXA,";
        if(SAdd.length() < 7) {	//收信地址，不足7位补0
            count = 7 - SAdd.length();
            for(int i = 0; i< count; i++)
                Str += "0";
        }
        Str += SAdd;
        Str += ",";
        Str += Mode;	    //通信方式 0=特快通信 1=普通通信
        Str += ",";
        Str += Type;			//报文传输方式 0=汉字 1=代码 2=混发
        Str +=",";
        byte[] temp, temp1;
        if(Type == 0) {
            Str += Content;
        }
        if(Type == 1) {
            Str += Content;
        }
        if(Type == 2) {
            Str += "A4";
            try {
                temp = Content.getBytes("gb2312");
                temp1 = new byte[temp.length*2];
                int j = 0;
                for(int i = 0; i< temp.length; i++) {
                    temp1[j] = (byte) ((temp[i] >> 4)&0x0F);
                    temp1[j + 1] = (byte) (temp[i]&0x0F);
                    j += 2;
                }
                for(int i = 0; i < temp1.length; i++){
                    if(temp1[i] <= 9 && temp1[i] >= 0)
                        temp1[i] += 0x30;
                    else
                        temp1[i] += 0x37;
                }

                Str += new String(temp1);

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Str += "*";
        byte[] test;
        byte test1;
        try {
            test = Str.getBytes("gb2312");
            test1 = xor(test);						//校验和
            int x = test1 & 0xFF;
            int c,d;
            if(((x>>4)&0x0F) >= 0 && ((x>>4)&0x0F) <= 9)
                c = (byte) (((x>>4)&0x0F) + 0x30);
            else
                c = (byte) (((x>>4)&0x0F) + 0x37);
            if((x&0x0F) >= 0 && (x&0x0F) <= 9)
                d = (byte) ((x&0x0F) + 0x30);
            else
                d = (byte) ((x&0x0F) + 0x37);
            byte[] y = {(byte) c, (byte) d};
            Str += new String(y);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//		Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }
    /**
     * 发送ICA 设备信息检测
     * @param IType 指令类型 =0，2，3，4，5，6，7，8 时，帧号字段固定填0
     * 							指令类型=1 时，帧号=0，终端输出所有下属用户授权信息，
     * 													当帧号取值为1~125 时，终端输出相应帧号对应的下属用户授权信息，
     * 													1-40 个下属用户授权信息为第1 帧，41-80 个下属用户授权信息为第2 帧，以此类推。
     * @param IFrame 帧号
     * @return String Str = 组包后报文
     * */
    public static String BD_SendICA(int IType, int IFrame) {
        String Str;
        String tempFrame;
        int count = 0;
        tempFrame = Integer.toString(IFrame);
        Str = "$CCICA,";
        Str += IType;				//指令类型
        Str += ",";
        if(IType != 1)
            Str +="000";
        else {
            if(tempFrame.length() < 3) {
                count = 3 - tempFrame.length();
                for(int i = 0; i < count; i++)
                    Str += "0";
                Str += IFrame;
            }
        }
        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }
    /**
     * 发送GXM 设置、读取用户设备管理信息
     * @param Mode 指令类型 1=设置用户设备管理信息 2=读取用户设备管理信息
     * @param Value 管理信息 32个十六进制的ASCII字符
     * @return String Str= 组包后报文
     * */
    public static String BD_SendGXM(int Mode, String Value) {
        String Str;
        Str = "$CCGXM,";
        Str += Mode;
        Str += ",";
        if(Mode == 1)
            Str += Value;
        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }
    /**
     * 发送LZM 零值设置、读取
     * @param Mode 模式 1=读取设备零值 2=设置设备零值
     * @param Value 零值值
     * @return String Str =组包后报文
     * */
    public static String BD_SendLZM(int Mode, int Value) {
        String Str;
        Str = "$CCLZM,";
        Str += Mode;								//读取、设置模式
        Str +=",";
        if(Mode == 2)
            Str += Value;							//零值
        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }
    /**
     * 发送WBA 位置报告2申请
     * @param SAdd 用户地址
     * @param Indicator 高程指示 true=H(高空) false=L(普通)
     * @param Antenna 天线高
     * @param Freq 报告频度
     * @return String Str =组包后报文
     * */
    public static String BD_SendWBA(String SAdd, boolean Indicator, int Antenna, int Freq) {
        String Str;
        Str = "$CCWBA,";
        int len = SAdd.length();
        int count = 0;
        count = 7 - len;
        if(count != 0) {
            for(int i =0; i < count; i++)
                Str += "0";
        }
        Str += SAdd;
        Str += ",";
        if(Indicator == true)		//高程指示
            Str += "H";
        else
            Str += "L";
        Str += ",";
        Str += Antenna;				//天线高
        Str += ",";
        Str += Freq;						//频度
        Str += "*";
        Str += xor(Str);					//校验和
        Str +="\r\n";						//回车换行符
        return Str;
    }

    /**
     * 发送WAA 位置报告1申请
     * @param Type 信息类型 1=发送报告 0=接收报告
     * @param Freq 报告频度
     * @param SAdd	用户地址 发送报告时 为接收人地址 接收报告时 为发送人地址
     * @param STime 位置报告时间
     * @param Latitude 纬度
     * @param Dir1 纬度方向（N/S）
     * @param Longitude 经度
     * @param Dir2 经度方向（E/W）
     * @param Altitude 高程
     * @return String Str =组包后报文
     * */
    public static String BD_SendWAA(int Type, int Freq, String SAdd, String STime,
                                    double Latitude, String Dir1, double Longitude, String Dir2, double Altitude) {
        String Str;
        int count = 0;
        Str = "$CCWAA,";
        Str += Type;
        Str += ",";
        if(Type == 1)
            Str += Freq;
        Str += ",";
        count = 7 - SAdd.length();
        if(count != 0) {
            for (int i = 0; i < count; i++)
                Str += "0";
        }
        Str += SAdd;			//地址
        Str += ",";
        Str += STime;		//定位时间
        Str += ",";
        Str += Latitude;	//纬度
        Str += ",";
        Str += Dir1;			//纬度方向（N/S）
        Str += ",";
        Str += Longitude;	//经度
        Str += ",";
        Str += Dir2;			//经度方向（E/W）
        Str += ",";
        Str += Altitude;		//高程数据
        Str += ",";
        Str += "M";			//高程单位：M
        Str += "*";
        Str += xor(Str);		//校验和
        Str +="\r\n";			//回车换行符
        return Str;
    }





    /**
     * 判断返回信息类型
     * @param FrameRec 返回的报文
     * @return String[] frameStr = {有效长度，返回信息类型}
     * */
    public static String[] BD_CheckData(String FrameRec) {
        String reSign;
        String [] Str = FrameRec.split(",");
        reSign = Str[0];
        frameStr[0] = "2";
        frameStr[1] = reSign;
        return frameStr;
    }

    /**
     * 接收GXM，返回的用户设备管理信息
     * @param FrameRec 返回的报文
     * @return String[] frameStr = {有效长度，返回的用户设备管理信息}
     * */
    public static String[] BD_ReceiveGXM(String FrameRec) {
        String[] Str;
        String [] temp;
        String Value;
        Str = FrameRec.split(",");
        temp = Str[2].split("\\*");		//取出管理信息

        if(Str[1].equals("3"))
            Value = temp[0];
        else
            Value = "返回值错误";
        frameStr[0] = "2";
        frameStr[1] = Value;
        return frameStr;
    }
    /**
     * 接收LZM，返回设备零值
     * @param FrameRec 返回的报文
     * @return String[] frameStr = {有效长度，返回的设备零值}
     * */
    public static String[] BD_ReceiveLZM(String FrameRec) {
        String Value;
        String[] Str;
        String [] temp;
        Str = FrameRec.split(",");
        temp = Str[2].split("\\*");
        if(Str[1].equals("3"))
            Value = temp[0];
        else
            Value = "返回值错误";
        frameStr[0] = "2";
        frameStr[1] = Value;
        return frameStr;
    }
    /**
     * 接收BDBSI数据，波束状态信息
     * @param FrameRec 返回的报文
     * @return String[] frameStr 解析后的波束状态信息= {有效长度，响应波束号，时差波束号，波束1-波束10}
     *
     * */
    public static String[] BD_ReceiveBSI(String FrameRec) {
        String SXybs, SScbs, SPower1, SPower2, SPower3, SPower4,
                SPower5, SPower6, SPower7, SPower8, SPower9, SPower10;
        String [] Str, temp;
        Str = FrameRec.split(",|\\*");
        SXybs = Str[1];
        SScbs = Str[2];
        SPower1 = Str[3];
        SPower2 = Str[4];
        SPower3 = Str[5];
        SPower4 = Str[6];
        SPower5 = Str[7];
        SPower6 = Str[8];
        SPower7 = Str[9];
        SPower8 = Str[10];
        SPower9 = Str[11];
//        temp = Str[12].split("\\*");
        SPower10 = Str[12];
//
        frameStr[0] = "13";					//有效长度
        frameStr[1] = SXybs;
        frameStr[2] = SScbs;
        frameStr[3] = SPower1;		//波束1-10
        frameStr[4] = SPower2;
        frameStr[5] = SPower3;
        frameStr[6] = SPower4;
        frameStr[7] = SPower5;
        frameStr[8] = SPower6;
        frameStr[9] = SPower7;
        frameStr[10] = SPower8;
        frameStr[11] = SPower9;
        frameStr[12] = SPower10;
        return frameStr;
    }
    /**
     * 接收FKI 反馈信息
     * @param FrameRec 返回的报文
     * @return String[] frameStr={有效长度，指令名称，指令执行情况（Y/N），
     * 												频度设置指示（Y/N），发射抑制指示，等待时间}
     * */
    public static String[] BD_ReceiveFKI(String FrameRec) {
        String SSign, SResult, SFreq, SCode, SWait;
        String [] Str, temp;
        Str = FrameRec.split(",");
        SSign = Str[1]; 			//指令名称
        SResult = Str[2];		//指令执行情况（Y/N）
        SFreq = Str[3]; 			//频度设置指示（Y/N）
    	/*发射抑制指示
    	 *  0：抑制解除
    	 *  1：接收到系统的抑制指令，发射被抑制
    	 *  2：电量不足，发射被抑制
    	 *  3：设置为无线电静默，发射被抑制
    	 *  */
        SCode = Str[4];
        temp = Str[5].split("\\*");
        SWait = temp[0];					//等待时间

        frameStr[0] = "6";					//有效长度
        frameStr[1] = SSign;
        frameStr[2] = SResult;
        frameStr[3] = SFreq;
        frameStr[4] = SCode;
        frameStr[5] = SWait;

        return frameStr;
    }
    /**
     * 输出SBX 用户设备相关信息
     * @param FrameRec 返回的报文
     * @return String[] frameStr={有效长度，设备供应商名称，设备类型，
     * 												程序版本号，串口协议版本，ICD协议版本号，设备序列号，ID号 }
     * */
    public static String[] BD_ReceiveSBX(String FrameRec) {
        String [] Str, temp;
        String SName, SType, SProVerq, SUsbVerq, SIcdVerq, SSerial, SId;
        Str = FrameRec.split(",");
        SName = Str[1];
        SType = Str[2];
        SProVerq = Str[3];
        SUsbVerq = Str[4];
        SIcdVerq = Str[5];
        SSerial = Str[6];
        temp = Str[7].split("\\*");
        SId = temp[0];

        frameStr[0] = "8";
        frameStr[1] = SName;		//设备供应商名称
        frameStr[2] = SType;			//设备类型
        frameStr[3] = SProVerq;	//程序版本号
        frameStr[4] = SUsbVerq;	//串口协议版本
        frameStr[5] = SIcdVerq;	//ICD协议版本号
        frameStr[6] = SSerial;			//设备序列号
        frameStr[7] = SId;				//ID号
        return frameStr;
    }
    /**
     * 输出ICI  IC信息
     * @param FrameRec 返回的报文
     * @return String[] frameStr={有效长度，用户地址（ID号）,序列号，通播地址, 用户特征值
     * 															,服务频度, 通信等级, 加密标志, 下属用户数}
     * */
    public static String[] BD_ReceiveICI(String FrameRec) {
        String [] Str, temp;
        String SAdd, SSerial, SBid, SFeature, SFreq, SLevel, SFlag, SNum;
        Str = FrameRec.split(",");
        SAdd = Str[1];
        SSerial = Str[2];
        SBid = Str[3];
        SFeature = Str[4];
        SFreq = Str[5];
        SLevel = Str[6];
        SFlag = Str[7];
        temp = Str[8].split("\\*");
        SNum = temp[0];

        frameStr[0] = "9";
        frameStr[1] = SAdd;					//用户地址（ID号）
        frameStr[2] = SSerial;				//序列号
        frameStr[3] = SBid;					//通播地址
        frameStr[4] = SFeature;				//用户特征值
        frameStr[5] = SFreq;					//服务频度
        frameStr[6] = SLevel;					//通信等级
        frameStr[7] = SFlag;					//加密标志
        frameStr[8] = SNum;					//下属用户数
        return frameStr;
    }
    /**
     * 输出HZR 用户设备通信回执查询后的回执信息
     * @param FrameRec 返回的报文
     * @return String[] frameStr={有效长度，用户地址（ID号）,回执数，回执一发信时间, 回执一回执时间
     * 															,回执二发信时间, 回执二回执时间, 回执三发信时间, 回执三回执时间
     * 															回执四发信时间, 回执四回执时间，回执五发信时间, 回执五回执时间}
     * */
    public static String[] BD_ReceiveHZR(String FrameRec) {
        String [] Str, temp;
        String SAdd, SReNum, SSendTime1,SReceTime1, SSendTime2,SReceTime2, SSendTime3,SReceTime3,
                SSendTime4,SReceTime4, SSendTime5,SReceTime5;
        Str = FrameRec.split(",");
        SAdd = Str[1];
        SReNum = Str[2];
        SSendTime1 = Str[3];
        SReceTime1 = Str[4];
        SSendTime2 = Str[5];
        SReceTime2 = Str[6];
        SSendTime3 = Str[7];
        SReceTime3 = Str[8];
        SSendTime4 = Str[9];
        SReceTime4 = Str[10];
        SSendTime5 = Str[11];
        temp = Str[12].split("\\*");
        SReceTime5 = temp[0];

        frameStr[0] = "13";					//有效长度
        frameStr[1] = SAdd;					//用户地址
        frameStr[2] = SReNum;				//回执数
        frameStr[3] = SSendTime1;		//回执1发信时间
        frameStr[4] = SReceTime1;		//回执1回执时间
        frameStr[5] = SSendTime2;		//回执2发信时间
        frameStr[6] = SReceTime2;		//回执2回执时间
        frameStr[7] = SSendTime3;		//回执3发信时间
        frameStr[8] = SReceTime3;		//回执3回执时间
        frameStr[9] = SSendTime4;		//回执4发信时间
        frameStr[10] = SReceTime4;		//回执4回执时间
        frameStr[11] = SSendTime5;		//回执5发信时间
        frameStr[12] = SReceTime5;		//回执5回执时间

        return frameStr;
    }
    /**
     * 接收DWR 定位信息
     * @param FrameRec 返回的报文
     * @return String[] frameStr={有效长度，定位信息类型，用户地址，定位时刻，纬度，纬度方向
     * 												经度，经度方向，大地高，高程异常，精度指示，紧急定位指示，多值解指示，高程类型指示｝
     * */
    public static String[] BD_ReceiveDWR(String FrameRec) {
        String [] Str, temp;
        String []wd, jd;
        String SType, SAdd, STime, SLatitude, SDir1, SLongitude, SDir2,
                SAltitude, SAbnormal, SAccuracy, SLoc, SMulti, SIndicator;
        Str = FrameRec.split(",");
        SType = Str[1];
        SAdd = Str[2];
        STime = Str[3];
        wd = Str[4].split("\\.");
        jd = Str[6].split("\\.");
        SLatitude = wd[0].substring(0, 2) + "度" + wd[0].substring(2)+ "." + wd[1].substring(0, 2) + "分";
        SDir1 = Str[5];
        SLongitude = jd[0].substring(0, 3) + "度" + jd[0].substring(3) +"." + jd[1].substring(0, 2) + "分";
        SDir2 = Str[7];
        SAltitude = Str[8];
        SAbnormal = Str[10];
        SAccuracy = Str[12];
        SLoc = Str[13];
        SMulti = Str[14];
        temp = Str[15].split("\\*");
        SIndicator = temp[0];
      /* 定位信息类型  1=本用户设备进行定位申请返回的定位信息，用户地址为本设备用户地址
        	      				2=具备指挥功能的用户设备进行定位查询返回的下属用户位置信息，用户地址为被查询用户的用户地址
        	     				3=接收到位置报告的定位信息，用户地址为发送位置报告方的用户地址
       */
        frameStr[0] = "14";					//有效长度
        frameStr[1] = SType;					//定位信息类型
        frameStr[2] = SAdd;					//用户地址
        frameStr[3] = STime;					//定位时刻
        frameStr[4] = SLatitude;			//纬度
        frameStr[5] = SDir1;					//纬度方向
        frameStr[6] = SLongitude;			//经度
        frameStr[7] = SDir2;					//经度方向
        frameStr[8] = SAltitude;			//大地高
        frameStr[9] = SAbnormal;			//高程异常
        frameStr[10] = SAccuracy;			//精度指示
        frameStr[11] = SLoc;					//紧急定位指示
        frameStr[12] = SMulti;				//多值解指示
        frameStr[13] = SIndicator;			//高程类型指示
        return frameStr;
    }
    /**
     * 接收TXR 通信信息
     * @param FrameRec 返回的报文
     * @return String[] frameStr={有效长度，信息类别，用户地址，电文形式，发信时间，通信电文内容}
     * */
    public static String[] BD_ReceiveTXR(String FrameRec) {
        String [] Str, temp;
        String SType, SAdd, SMode, STime, SContent;
        Str = FrameRec.split(",");

        SType = Str[1];		//信息类别
        SAdd = Str[2];		//用户地址
        SMode = Str[3];	//电文形式
        STime = Str[4];		//发信时间
        temp = Str[5].split("\\*");
        SContent = temp[0];
        String Receive1,Receive2, Strre = "";
        if(SMode.equals("2")) {				//混发
            SContent = SContent.substring(2);
            for(int i = 0; i < SContent.length(); ) {
                if(i + 4 <= SContent.length()) {
                    Receive1 = SContent.substring(i, i + 2);
                    Receive2 = SContent.substring(i + 2, i + 4);
                }else{
                    Receive1 = "0";			//奇数位高位补0
                    Receive2 = SContent.substring(i, i + 2);
                }
                try {
                    byte a = (byte)Integer.parseInt(Receive1, 16);
                    byte b = (byte)Integer.parseInt(Receive2, 16);
                    byte []rec = {a , b};
                    Strre += new String(rec , "gb2312");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                i+=4;
            }
            SContent = Strre;
        }

        frameStr[0] = "";
        frameStr[1] = SType;
        frameStr[2] = SAdd;
        frameStr[3] = SMode;
        frameStr[4] = STime;
        frameStr[5] = SContent;
        return frameStr;
    }


    /***************新添加 by roc*********************************************************/

    /**
     * 工作模式设置
     * @param rdss
     * @param rnss
     * @return
     */
    public static String BD_SendMDS(int rdss , int rnss)
    {
        String Str;
        Str = "$CCMDS,";
        Str += rdss;
        Str +=",";
        Str+=rnss+",";
        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 工作模式查询
     * @return
     */
    public static String BD_SendMDQ()
    {
        String Str;
        Str = "$CCMDQ,";

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 自动数据报告参数设置
     * @param address
     * @param mode
     * @param freq
     * @param num
     * @return
     */
    public static String BD_SendPRS(String address , int mode , int freq , int num)
    {
        String Str;
        Str = "$CCPRS,";
        Str+= addZero(address, 7)+",";
        Str+= mode+",";
        Str+= addZero(freq, 5)+",";
        Str+= addZero(num, 2)+",";

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 自动数据报告参数查询
     * @return
     */
    public static String BD_SendPRQ()
    {
        String Str;
        Str = "$CCPRQ,";


        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 用户组设置
     * @param type
     * @param num
     * @param id 8位
     * @return
     */
    public static String BD_SendUGS(int type , int num , String[] id)
    {
        String Str;
        Str = "$CCUGS,";
        Str+=type+",";
        Str+=num+",";
        for(int i=0;i<num;i++)
        {
            Str+=id[i]+",";
        }
        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 用户组查询
     * @param type
     * @return
     */
    public static String BD_SendUGQ(int type)
    {
        String Str;
        Str = "$CCUGQ,";
        Str+=type+",";

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 版本查询
     * @param type
     * @return
     */
    public static String BD_SendVRQ(int type)
    {
        String Str;
        Str = "$CCVRQ,";

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 设置蓝牙名
     * @param nameBT
     * @return
     */
    public static String BD_SendBTI(String nameBT)
    {
        String Str;
        Str = "$CCBTI,";
        Str+=nameBT+",";

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 设置串口波特率
     * @param index 0-7
     * @return
     */
    public static String BD_SendCOM(int index)
    {
        String Str;
        Str = "$CCCOM,";
        Str+=index+",";

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 工作模式信息$BDMDX
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveMDX(String FrameRec) {
        String [] Str;
        String rdss,rnss;
        Str = FrameRec.split(",");
        rdss = Str[1];
        rnss = Str[2];


        frameStr[0] = "3";
        frameStr[1] = rdss;
        frameStr[2] = rnss;

        return frameStr;
    }

    /**
     * 自动数据上报参数信息$BDPRX
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceivePRX(String FrameRec) {
        String [] Str;
        String address,mode,freq,num;
        Str = FrameRec.split(",");
        address = Str[1];
        mode = Str[2];
        freq = Str[3];
        num = Str[4];


        frameStr[0] = "5";
        frameStr[1] = address;
        frameStr[2] = mode;
        frameStr[3] = freq;
        frameStr[4] = num;


        return frameStr;
    }

    /**
     * 用户组信息$BDUGX
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveUGX(String FrameRec) {
        String [] Str,tempId;
        String type,num;
        Str = FrameRec.split(",");
        type = Str[1];
        num = Str[2];
        for(int i = 0; i< CommonUtil.Str2int(num); i++)
        {
            frameStr[3+i]=Str[3+i];
        }


        frameStr[0] = (3+CommonUtil.Str2int(num))+"";
        frameStr[1] = type;
        frameStr[2] = num;



        return frameStr;
    }

    /**
     * 版本信息$BDVRX
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveVRX(String FrameRec) {
        String [] Str,tempId;
        String ver;
        Str = FrameRec.split(",");
        ver = Str[1];

        frameStr[0] = "2";
        frameStr[1] = ver;

        return frameStr;
    }

    /**
     * 蓝牙设置结果$BDBTX
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveBTX(String FrameRec) {
        String [] Str,tempId;
        String state;
        Str = FrameRec.split(",");
        state = Str[1];

        frameStr[0] = "2";
        frameStr[1] = state;

        return frameStr;
    }

    /**
     * 指令执行信息$BDCEX
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveCEX(String FrameRec) {
        String [] Str,tempId;
        String state , order , wait;
        Str = FrameRec.split(",");
        state = Str[1];
        order = Str[2];
        wait =Str[3];

        frameStr[0] = "4";
        frameStr[1] = state;
        frameStr[2] = order;
        frameStr[3] = wait;

        return frameStr;
    }

    /**************RNSS相关 by roc *************************************************/

    /**
     * RNSS输出语句频度设置
     * @param freGGA
     * @param freGSV
     * @param freGLL
     * @param freGSA
     * @param freRMC
     * @param freZDA
     * @return
     */
    public static String BD_SendRNS(int freGGA , int freGSV ,int freGLL ,int freGSA ,int freRMC ,int freZDA )
    {
        String Str;
        Str = "$CCRNS,";
        Str+= addZero(freGGA, 4)+",";
        Str+=  addZero(freGSV, 4)+",";
        Str+=  addZero(freGLL, 4)+",";
        Str+=  addZero(freGSA, 4)+",";
        Str+=  addZero(freRMC, 4)+",";
        Str+=  addZero(freZDA, 4)+",";

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     *  RNSS输出语句频度查询
     * @return
     */
    public static String BD_SendRNQ()
    {
        String Str;
        Str = "$CCRNQ,";

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }


    /**
     * RNSS复位
     * @param mode 0-2
     * @return
     */
    public static String BD_SendRNR(int mode)
    {
        String Str;
        Str = "$CCRNR,";
        Str += mode;

        Str += "*";
        Str += xor(Str);								//校验和
        Str +="\r\n";									//回车换行符
        return Str;
    }

    /**
     * 复位信息$BDRSX
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveRSX(String FrameRec) {
        String [] Str;
        String rdss,rnss;
        Str = FrameRec.split(",");

        frameStr[0] = "1";


        return frameStr;
    }

    /**
     * 语句输出频度信息$BDRNX
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveRNX(String FrameRec) {
        String [] Str;
        String freGGA , freGSV , freGLL , freGSA , freRMC , freZDA;
        Str = FrameRec.split(",");

        frameStr[0] = "7";
        freGGA=Str[1];
        freGSA=Str[2];
        freGLL=Str[3];
        freGSA=Str[4];
        freRMC=Str[5];
        freZDA=Str[6];

        frameStr[1]=Str[1];
        frameStr[2]=Str[2];
        frameStr[3]=Str[3];
        frameStr[4]=Str[4];
        frameStr[5]=Str[5];
        frameStr[6]=Str[6];

        return frameStr;
    }

    /**
     * 输出GGA
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveGGA(String FrameRec)
    {
        String [] Str, temp;
        Str = FrameRec.split(",");
        frameStr[0]="15";
        frameStr[1]=Str[1]; //定位时刻
        frameStr[2]=Str[2]; //纬度
        frameStr[3]=Str[3]; //纬度方向
        frameStr[4]=Str[4]; //经度
        frameStr[5]=Str[5]; //经度方向
        frameStr[6]=Str[6]; //状态指示
        frameStr[7]=Str[7]; //卫星数
        frameStr[8]=Str[8]; //HDOP
        frameStr[9]=Str[9]; //天线大地高
        frameStr[10]=Str[10]; //大地高单位
        frameStr[11]=Str[11]; //高程异常
        frameStr[12]=Str[12]; //高程异常单位
        frameStr[13]=Str[13]; //差分数据龄期

        frameStr[14]=Str[14].split("\\*")[0]; //VDOP

        return frameStr;
    }

    /**
     * 输出GLL
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveGLL(String FrameRec)
    {
        String [] Str, temp;
        Str = FrameRec.split(",");
        frameStr[0]="8";
        frameStr[1]=Str[1]; //纬度
        frameStr[2]=Str[2]; //纬度方向
        frameStr[3]=Str[3]; //经度
        frameStr[4]=Str[4]; //经度方向
        frameStr[5]=Str[5]; //UTC时间
        frameStr[6]=Str[6]; //数据状态
        frameStr[7]=Str[7].split("\\*")[0]; //模式指示

        return frameStr;
    }

    /**
     * 输出RMC
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveRMC(String FrameRec)
    {
        String [] Str, temp;
        Str = FrameRec.split(",");
        frameStr[0]="13";
        frameStr[1]=Str[1]; //时间UTC
        frameStr[2]=Str[2]; //定位状态
        frameStr[3]=Str[3]; //纬度
        frameStr[4]=Str[4]; //纬度方向
        frameStr[5]=Str[5]; //经度
        frameStr[6]=Str[6]; //经度方向
        frameStr[7]=Str[7]; //地面速度
        frameStr[8]=Str[8]; //地面航向
        frameStr[9]=Str[9]; //日期
        frameStr[10]=Str[10]; //磁偏角
        frameStr[11]=Str[11]; //磁偏角方向
        frameStr[12]=Str[12].split("\\*")[0]; //模式指示

        return frameStr;
    }

    /**
     * 输出GSA
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveGSA(String FrameRec)
    {
        String [] Str, temp;
        Str = FrameRec.split(",");
        frameStr[0]="18";
        frameStr[1]=Str[1]; //模式指示
        frameStr[2]=Str[2]; //选用模式
        frameStr[3]=Str[3]; //PRN-1
        frameStr[4]=Str[4]; //PRN-2
        frameStr[5]=Str[5]; //PRN-3
        frameStr[6]=Str[6]; //PRN-4
        frameStr[7]=Str[7]; //PRN-5
        frameStr[8]=Str[8]; //PRN-6
        frameStr[9]=Str[9]; //PRN-7
        frameStr[10]=Str[10]; //PRN-8
        frameStr[11]=Str[11]; //PRN-9
        frameStr[12]=Str[12]; //PRN-10
        frameStr[13]=Str[13]; //PRN-11
        frameStr[14]=Str[14]; //PRN-12
        frameStr[15]=Str[15]; //PDOP
        frameStr[16]=Str[16]; //HDOP

        frameStr[17]=Str[17].split("\\*")[0]; //VDOP

        return frameStr;
    }

    /**
     * 输出GSV
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveGSV(String FrameRec)
    {
        String [] Str, temp;
        Str = FrameRec.split(",");
        frameStr[0]=Str.length+"";
        frameStr[1]=Str[1]; //语句总数
        frameStr[2]=Str[2]; //语句序号
        frameStr[3]=Str[3]; //卫星数
        for(int i=0;i<(Str.length-4)/4;i++)
        {
            frameStr[4+i*4]=Str[4+i*4]; //卫星号
            frameStr[5+i*4]=Str[5+i*4]; //仰角
            frameStr[6+i*4]=Str[6+i*4]; //方位角
            frameStr[7+i*4]=Str[7+i*4].split("\\*")[0]; //信噪比
        }

        return frameStr;
    }

    /**
     * 输出ZDA
     * @param FrameRec
     * @return
     */
    public static String[] BD_ReceiveZDA(String FrameRec)
    {
        String [] Str, temp;
        Str = FrameRec.split(",");
//        frameStr[0]="7";
//        frameStr[1]=Str[1]; //模式指示
//        frameStr[2]=Str[2]; //UTC时间
//        frameStr[3]=Str[3]; //日
//        frameStr[4]=Str[4]; //月
//        frameStr[5]=Str[5]; //年
//		frameStr[6]=Str[6]; //本地时区
//		frameStr[7]=Str[7]; //时区分钟差
//		frameStr[8]=Str[8]; //定时修正值时刻
//		frameStr[9]=Str[9]; //修正值
//		frameStr[10]=Str[10]; //精度指示
//        frameStr[6]=Str[6].split("\\*")[0]; //信号锁定

        return frameStr;
    }

    /**
     * @param FrameRec 返回的报文
     * @return String[] frameStr={有效长度，用户地址，报告时间，纬度，纬度方向，经度，经度方向，高程，高程单位}
     **/
    public static String[] BD_ReceiveWAA(String FrameRec)
    {
        String [] Str;
        Str = FrameRec.split(",");
        frameStr[0]="9";
        frameStr[1]=Str[2]; //用户地址
        frameStr[2]=Str[3]; //报告时间
        frameStr[3]=Str[4]; //纬度
        frameStr[4]=Str[5]; //纬度方向
        frameStr[5]=Str[6]; //经度
        frameStr[6]=Str[7]; //经度方向
        frameStr[7]=Str[8]; //高程值
        frameStr[8]=Str[9].split("\\*")[0]; //高程单位


        return frameStr;
    }

    /**
     * 前方补零
     * @param num
     * @param len
     * @return
     */
    public static String addZero(int num , int len)
    {
        String Str;
        Str=num+"";
        while(len>Str.length())
        {
            Str = "0"+Str;
        }
        return Str;
    }

    public static String addZero(String num , int len)
    {
        String Str;
        Str=num;
        while(len>Str.length())
        {
            Str = "0"+Str;
        }
        return Str;
    }
}
