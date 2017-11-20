package com.xyxl.tianyingn3.solutions;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.MessageBean;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.TestMsg;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rocgoo on 2017/11/9 下午1:46.
 * Function：用于保存收到的数据并进行分析处理,单例
 */

public class BufferHandle {

    private Context mContext;

    //缓冲区相关 new analyse by LELE
    final int RDSS_FIFO_LEN = 8192;
    final int RDSS_LEN_MASK = RDSS_FIFO_LEN - 1;
    final int BDFrameLenMax = 512;
    final int BDFrameLenMin = 9;
    final int BDFrameArrayListMaxLen = 16;
    private int BProIndex = 0x00;
    private int XorTrace = 0x00;

    private List<String> FrameList = new ArrayList<String>(BDFrameArrayListMaxLen);
    ;
    private byte[] FrameBuffer = new byte[BDFrameLenMax];//数据帧临时缓冲区 //数据帧临时缓冲区
    private byte[] RawDataFIFO = new byte[RDSS_FIFO_LEN];
    private int FIFOWrIndex = 0;
    private int FIFORdIndex = 0;


    private static BufferHandle instance;

    public static BufferHandle getInstance()
    {
        if(instance == null)
        {
            instance = new BufferHandle();

        }
        return instance;
    }

    public void setContext(Context context)
    {
        this.mContext = context;
    }

    /**************缓冲区方法开始*****************************************************************************/

    private void BDCmdFIFO_Init()/*缓冲区初始化*/
    {
        FIFOWrIndex = 0;
        FIFORdIndex = 0;
    }
    private int Set_WriteDataToFifo(byte[] iSourceBuf, int CopyLen)/*数据写入缓冲区:*/
    {
        int iEmptyLen = RDSS_FIFO_LEN - Get_FifoCounts();
        int iWriteLen = 0x00;
        if (Get_IsFifoFull() == true) return 0;

        if (CopyLen >= iEmptyLen)
        {
            iWriteLen = iEmptyLen;
        }
        else
        {
            iWriteLen = CopyLen;
        }
        for (int i = 0; i < iWriteLen; i++)
        {
            RawDataFIFO[RDSS_LEN_MASK & (FIFOWrIndex++)] = iSourceBuf[i];
        }
        return (iWriteLen);
    }

    private int Get_ReadDataFromFifo(byte[] iDesBuf, int iRequestLen)
    {
        int iDataCounts = Get_FifoCounts();
        int iReadLen = 0;
        if (iDataCounts == 0) return 0;

        if (iDataCounts >= iRequestLen)
        {
            iReadLen = iRequestLen;
        }
        else
        {
            iReadLen = iDataCounts;
        }
        for (int i = 0; i < iReadLen; i++)
        {
            iDesBuf[i] = RawDataFIFO[RDSS_LEN_MASK & (FIFORdIndex++)];
        }
        return iReadLen;
    }

    private int Get_FifoCounts()/*获取缓冲区中的数据量*/
    {
        return (RDSS_LEN_MASK & (FIFOWrIndex - FIFORdIndex));
    }

    private boolean Get_IsFifoEmpty()/*检查缓冲区是否为空:true 缓冲区空，否则缓冲区不空*/
    {
        if (Get_FifoCounts() == 0)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    private boolean Get_IsFifoFull() /*缓冲区是否已满 true 表示满，false表示 不满*/
    {
        if (Get_FifoCounts() == RDSS_LEN_MASK)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private byte  HexASCII_To_HexVal( byte Data ) //将16进制字符串转换成对应16进制数据
    {
        if( (Data>=0x30) && (Data<=0x39))
        {
            return  (byte)(Data-0x30);
        }
        else if(Data==0x41 )
        {
            return 0x0A ;
        }
        else if(Data==0x42 )
        {
            return 0x0B ;
        }
        else if(Data==0x43 )
        {
            return 0x0C ;
        }
        else if(Data==0x44 )
        {
            return 0x0D ;
        }
        else if(Data==0x45 )
        {
            return 0x0E ;
        }
        else// if(Data==0x46 )
        {
            return 0x0F ;
        }
    }

    public  int AnalyseGetBDFrameCount()
    {
        return FrameList.size() ;
    }


    public synchronized String AnalyseGetBDframe(  )
    {
        try
        {
            String IFrameRead= FrameList.get(0).toString();
            FrameList.remove(0);
            return IFrameRead;
        }
        catch(Exception e)
        {
//            LogUtil.e(e.toString());
            return null;
        }

    }

    public synchronized void RawDataInput( byte [] Input,int len  )
    {
        Set_WriteDataToFifo(Input,len);
    }

    public void RawDataInput_Init( )
    {
        BDCmdFIFO_Init();
    }

    public void AnalyseFrame_Init()
    {
        BProIndex =0;
    }

    /**
     * 分析2.1缓冲区数据，得到有效指令
     * @return 有效数据列表
     */
    public synchronized void AnalyseBuffer() {

        byte [] DataRead = new byte [1];
        try {
            while ( (Get_IsFifoEmpty()==false)  &&  (FrameList.size() < BDFrameArrayListMaxLen)) {
                Get_ReadDataFromFifo(DataRead,1);//RawDataRecv.get(0);
                FrameBuffer[BProIndex] =  DataRead[0] ;
                if (BProIndex == 0x00) {
                    if (DataRead[0]  == 0x24) {
                        BProIndex = 1;
                    }
                } else {
                    if (BProIndex >= BDFrameLenMax - 1) {
                        BProIndex = 0;
                        break;
                    } else {
                        if ( (FrameBuffer[BProIndex] ==0x0A)
                                &&( FrameBuffer[BProIndex - 1] == 0x0D )) {
                            String iNew =new String(FrameBuffer,0,BProIndex-1);
                            //FrameString=iNew+"\r\n" ;
                            FrameList.add(iNew+"\r\n");
//                            sendData(iNew+"\r\n");
                            BProIndex = 0;
                            break;
                        } else {
                            BProIndex++;
                        }
                    }
                }
            }
        } catch (Exception e) {
//            if(mContext != null)
//            {
//                LogUtil.e(mContext.getResources().getString(R.string.data_analyse_error)+":偏移BProIndex:"+BProIndex);
//
//            }
            BProIndex = 0;
            e.printStackTrace();
        }


    }

    /**************缓冲区方法结束*****************************************************************************/

    //数据分析解析方法
    /**
     * 将有效指令进行分类处理
     * @param s 有效指令字符串
     * @return 有效数据字符串组
     */
    public Object sendData(String s)
    {
        String[] checkStr=BdSdk_v2_1.BD_CheckData(s);
        String[] Str;
//		Log.i("send",checkStr[1]);
        //判断为IC信息
        if(checkStr[1].equals("$BDICI"))
        {
            Str=BdSdk_v2_1.BD_ReceiveICI(s);

            //向全局变量添加IC信息
            BdCardBean.getInstance().setIdNum(BdCardBean.FormatCardNum(Str[1]));
            BdCardBean.getInstance().setServiceFrequency(CommonUtil.Str2int(Str[5]));
            BdCardBean.getInstance().setCardLv(CommonUtil.Str2int(Str[6]));
            LogUtil.i(BdCardBean.getInstance().toString());

            return BdCardBean.getInstance();
        }
        //判断为波束信息
        else if(checkStr[1].equals("$BDBSI"))
        {
            Str=BdSdk_v2_1.BD_ReceiveBSI(s);
//            LogUtil.i(s);
            int[] tmp = new int[10];
            for(int i=0;i<tmp.length;i++)
            {
                int tmpInt = CommonUtil.Str2int(Str[3+i]);
                if(tmpInt>=0 && tmpInt<=4)
                {
                    tmp[i] = tmpInt;
                }
                else
                {
                    tmp[i] = 0;
                }

            }
            BdCardBean.getInstance().setBeamLvs(tmp);

            return BdCardBean.getInstance();
//            int tem4=0;
//            int tem3=0;
//            for(int i=0;i<10;i++)
//            {
////                globalData.setBeamLv(i, CommonUtil.Str2int(Str[3+i]));
//                if(CommonUtil.Str2int(Str[3+i])==4)
//                {
//                    tem4++;
//                }
//                else if(CommonUtil.Str2int(Str[3+i])==3)
//                {
//                    tem3++;
//                }
//
//            }
//
//            //判断RDSS信号强度
//            if((tem4>1&&tem3>0)||tem4>2)
//            {
////                globalData.setRdssSignalLv(2);
//            }
//            else if(tem4<1&&tem3<3)
//            {
////                globalData.setRdssSignalLv(0);
//            }
//            else
//            {
////                globalData.setRdssSignalLv(1);
//            }

        }
        //判断反馈信息
        else if(checkStr[1].equals("$BDFKI"))
        {
            Str=BdSdk_v2_1.BD_ReceiveFKI(s);
            LogUtil.i("测试发送反馈"+s);
            TestMsg.getInstance().AddSendOkNum();
            //指令执行成功时
            if(Str[2].equals("Y"))
            {

            }
            else
            {
                LogUtil.i("测试发送失败"+Str[1]+","+Str[5]+"秒后请重试");

            }

            return "测试发送反馈"+s;

        }
        //判断定位信息
        else if(checkStr[1].equals("$BDDWR"))
        {
            Str=BdSdk_v2_1.BD_ReceiveDWR(s);
//            globalData.setRdssLon(GpsData.formatBTRdssPos(Str[6]));
//            globalData.setRdssLat(GpsData.formatBTRdssPos(Str[4]));
//            globalData.setRdssAn(Str[8]+"米");
        }
        //判断通信信息
        else if(checkStr[1].equals("$BDTXR"))
        {
            LogUtil.i("测试接收");
            Str=BdSdk_v2_1.BD_ReceiveTXR(s);
            if(Str[4]==null||Str[4].length()<2)
            {
                //保存毫秒时间数据
                Str[4] = System.currentTimeMillis()+"";
            }

            //测试用
            TestMsg.getInstance().AddRcvNum();
//            MessageBean messageBean = new MessageBean();
//            messageBean.setSendAddress(BdCardBean.getInstance().getIdNum());
//            messageBean.setMsgCon(Str[5]);

            Message_DB msgDb = new Message_DB();
            try
            {
                msgDb.setRcvAddress(BdCardBean.getInstance().getIdNum());
                msgDb.setRcvUserName(BdCardBean.getInstance().getIdNum());
                msgDb.setSendAddress(Str[2]);
                msgDb.setSendUserName(Str[2]);
                msgDb.setMsgTime(System.currentTimeMillis());
                msgDb.setMsgType(1);
                msgDb.setMsgSendStatue(1);
                msgDb.setMsgCon(Str[5]);
                msgDb.setMsgPos("");
                msgDb.setRemark("");
                msgDb.setDelFlag(0);

                long _id = msgDb.save();
                LogUtil.i(_id+" saved");

            }
            catch(Exception e)
            {
                LogUtil.e(e.toString());
            }

            return msgDb;


//            if(globalData.getAppWorkType() == MORE_REPORT&&Str[5].contains("SJXX"))
//            {
//                globalData.setTimeOfRcv(globalData.getTimeOfRcv()+1);
//            }
//            //插入短信息数据表
//            new Insert(mContext).insertSMS(SMS_RECEIVE, Str[2], globalData.getLocalID(), Str[4], Str[5], "");
//            globalData.addNewSmsId(Str[2]);
//            globalData.setToastStr("收到一条来自 "+ Str[2] + " 的短消息");
//
//            if(!Str[5].startsWith("回置信息"))
//            {
//                delaySendSms(Str[2], 0);
//            }
        }

        //判断回执信息
        else if(checkStr[1].equals("$BDHZR"))
        {
            Str=BdSdk_v2_1.BD_ReceiveHZR(s);

        }
        //判断WAA位置上报
        else if(checkStr[1].equals("$BDWAA"))
        {
            Str=BdSdk_v2_1.BD_ReceiveWAA(s);

        }
        //BDCEX指令执行信息
        else if(checkStr[1].equals("$BDCEX"))
        {
            Str=BdSdk_v2_1.BD_ReceiveCEX(s);
//            globalData.setToastStr("执行状态"+Str[1]+" 执行指令"+Str[2]+" 等待时间"+Str[3]);
        }
        //连续上报信息
        else if(checkStr[1].equals("$BDPRX"))
        {
            Str=BdSdk_v2_1.BD_ReceivePRX(s);
            SharedPreferences.Editor edi=mContext.getSharedPreferences("Report", Context.MODE_WORLD_WRITEABLE).edit();
            edi.putString("reportFreq", Str[3]);
            edi.putString("reportAddress", Str[1]);
            edi.commit();
            if(Str[2].equals("1")||Str[2].equals("2")||Str[2].equals("3"))
            {
//                globalData.setAppWorkType(BD_AUTO_REPORT);
//                globalData.setToastStr("设备正在向"+Str[1]+"进行位置上报,频度"+Str[3]+",模式"+Str[2]);

            }
//            else if(Str[2].equals("0")&&globalData.getAppWorkType()==BD_AUTO_REPORT)
//            {
//                globalData.setAppWorkType(NO_WORK);
//            }
        }
        //rnss定位数据
        else if(checkStr[1].equals("$GNGGA")||checkStr[1].equals("$BDGGA"))
        {
            //globalData.setRnssSignalLv(2);
            Str=BdSdk_v2_1.BD_ReceiveGGA(s);
//            globalData.setRnssLat(GpsData.formatSerialRnssPos(Str[2], 0));
//            globalData.setRnssLon(GpsData.formatSerialRnssPos(Str[4], 0));
//            globalData.setRnssAn(Str[9]);

        }
        //rnss GLL定位数据
        else if(checkStr[1].equals("$GNGLL")||checkStr[1].equals("$BDGLL")||checkStr[1].equals("$GPLL"))
        {
            Str=BdSdk_v2_1.BD_ReceiveGLL(s);
//            globalData.setRnssLat(GpsData.formatSerialRnssPos(Str[1], 0));
//            globalData.setRnssLon(GpsData.formatSerialRnssPos(Str[3], 0));
        }
        //rnss RMC定位数据
        else if(checkStr[1].equals("$GNRMC")||checkStr[1].equals("$BDRMC")||checkStr[1].equals("$GPRMC"))
        {
            try
            {
                Str=BdSdk_v2_1.BD_ReceiveRMC(s);
                //判断二代信息是否有效
                if(Str[2].equals("A"))
                {
                    MyPosition.getInstance().setType(1);
                }
                else
                {
                    MyPosition.getInstance().setType(0);
                }

//            globalData.setRnssVelocity(Double.valueOf(Str[7])*0.514+"");

                MyPosition.getInstance().setMyLon(GpsData.formatSerialRnssPos(Str[5], 0));
                MyPosition.getInstance().setMyLat(GpsData.formatSerialRnssPos(Str[3], 0));
                MyPosition.getInstance().setMyDir(CommonUtil.Str2float(Str[8]));
            }
            catch(Exception e)
            {

            }


        }
        //rnss GSA定位数据
        else if(checkStr[1].equals("$GNGSA")||checkStr[1].equals("$BDGSA")||checkStr[1].equals("$GPGSA"))
        {
            Str=BdSdk_v2_1.BD_ReceiveGSA(s);

        }
        //rnss BDGSV定位数据
        else if(checkStr[1].equals("$BDGSV"))
        {
            Log.i("BDGSV",s);
            try
            {
                Str=BdSdk_v2_1.BD_ReceiveGSV(s);
                //globalData.setBDSat(Str[3]);
                int index=CommonUtil.Str2int(Str[2]);

                for(int i=0;i<(CommonUtil.Str2int(Str[0])-4)/4;i++)
                {
                    if(Str[7+i*4].equals(""))
                    {
                        Str[7+i*4]="0";
                    }
//                bdSat[(index-1)*4+i]=Str[4+i*4]+"m"+Str[5+i*4]+"m"+Str[6+i*4]+"m"+Str[7+i*4]; //卫星号、仰角、方位角、载噪比

                }

                if(Str[2].equals(Str[1]))
                {
//                globalData.setBDSat(Str[3]);
//                for(int i=0;i<CommonUtil.Str2int(Str[3]);i++)
//                {
//                    globalData.setBDSat(globalData.getBDSat()+"m"+bdSat[i]);
//                }
                }
            }
            catch(Exception e)
            {

            }
        }
        //rnss GPGSV定位数据
        else if(checkStr[1].equals("$GPGSV"))
        {
            try
            {
                Str=BdSdk_v2_1.BD_ReceiveGSV(s);
                int index=CommonUtil.Str2int(Str[2]);

                for(int i=0;i<(CommonUtil.Str2int(Str[0])-4)/4;i++)
                {
                    if(Str[7+i*4].equals(""))
                    {
                        Str[7+i*4]="0";
                    }
//                gpsSat[(index-1)*4+i]=Str[4+i*4]+"m"+Str[5+i*4]+"m"+Str[6+i*4]+"m"+Str[7+i*4].split("\\*")[0]; //卫星号、仰角、方位角、载噪比

                }

                if(Str[2].equals(Str[1]))
                {
//                globalData.setGPSSat(Str[3]);
//                for(int i=0;i<CommonUtil.Str2int(Str[3]);i++)
//                {
//                    globalData.setGPSSat(globalData.getGPSSat()+"m"+gpsSat[i]);
//                }
                }
            }
            catch(Exception e)
            {

            }

        }
        //rnss ZDA定位数据
        else if(checkStr[1].equals("$GNZDA")||checkStr[1].equals("$BDZDA")||checkStr[1].equals("$GPZDA"))
        {
//            Str=BdSdk_v2_1.BD_ReceiveZDA(s);
//            Log.i("ZDA",Str[1].substring(0, 2)+":"+Str[1].substring(2, 4)+":"+Str[1].substring(4, 6));
//            globalData.setGpsDate(Str[4]+"-"+Str[3]+"-"+Str[2]);
//            globalData.setGpsTime(Str[1].substring(0, 2)+":"+Str[1].substring(2, 4)+":"+Str[1].substring(4, 6));
        }

        return null;
    }
}
