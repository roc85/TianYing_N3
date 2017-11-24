package com.xyxl.tianyingn3.bluetooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.BtConnectInfo;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.database.Notice_DB;
import com.xyxl.tianyingn3.global.App;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.global.TestMsg;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.service.MainService;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;
import com.xyxl.tianyingn3.solutions.BufferHandle;
import com.xyxl.tianyingn3.ui.activities.MainActivity;
import com.xyxl.tianyingn3.util.NotificationBroadcastReceiver;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

/**
 * Created by rocgoo on 2017/11/9 下午1:44.
 * Function：
 */

public class BluetoothService extends Service implements FinalDatas{

    //蓝牙相关变量
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    public BluetoothGattCharacteristic mNotifyCharacteristic;
    public BluetoothGattCharacteristic mNotifyCharacteristic_Write;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS ="DEVICE_ADDRESS";
    public final static UUID UUID_NOTIFY_READ =
            UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_NOTIFY_WRITE =
            UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SERVICE =
            UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    //蓝牙单UUID
    public final static UUID UUID_NOTIFY_SINGLE =
            UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SERVICE_SINGLE =
            UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");

    private DataAnalyseThread dataAnalyseThread;

    //通知栏相关
    private NotificationCompat.Builder mBuilder;
    private int     				   notifyId;
    public NotificationManager mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //注册到bus事件总线中
        AppBus.getInstance().register(this);
        LogUtil.i(getResources().getString(R.string.ble_service_bind));


        //自动连接
        if(initialize() && !TextUtils.isEmpty(SettingSharedPreference.getDataString(this,BT_DEVICE_MAC)))
        {
            connect(SettingSharedPreference.getDataString(this,BT_DEVICE_MAC));
            LogUtil.i(getResources().getString(R.string.ble_connect_start)+SettingSharedPreference.getDataString(this,BT_DEVICE_NAME));

        }

        //启动数据分析线程
        dataAnalyseThread = new DataAnalyseThread();
        dataAnalyseThread.isRunning = true;
        dataAnalyseThread.start();

        //初始化通知栏
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
//				.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量

                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);

        return null;
    }

    public void ShowNotice(String str , long _Id)
    {
        //通知栏显示
//        if(SettingSharedPreference.getNotic(mContext)==0)
//        {
//            try {
//                notifyId = Integer.valueOf(bdId);
//            } catch (Exception e) {
//                // TODO: handle exception
//                notifyId = 100;

        notifyId = (int) _Id;
        mNotificationManager.cancel(notifyId);

        mBuilder.setContentTitle(getResources().getString(R.string.new_msg_title))
                .setContentText(str)
                .setTicker(getResources().getString(R.string.new_msg_notice));//通知首次出现在通知栏，带上升动画效果的

        mNotificationManager.notify(notifyId, mBuilder.build());

        //新的通知
        Intent intentClick = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
        intentClick.setAction("notification_clicked");
        intentClick.putExtra("msg_id", _Id);
        intentClick.putExtra("notice_type", 0);
        intentClick.putExtra(NotificationBroadcastReceiver.TYPE, 1);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(getApplicationContext(), 0, intentClick, PendingIntent.FLAG_ONE_SHOT);

        Intent intentCancel = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
        intentCancel.setAction("notification_cancelled");
        intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, 2);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(getApplicationContext(), 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentTitle(getResources().getString(R.string.new_msg_title))
                .setContentText(str)
                .setTicker(getResources().getString(R.string.new_msg_notice))
                .setContentIntent(pendingIntentClick)
                .setDeleteIntent(pendingIntentCancel);//通知首次出现在通知栏，带上升动画效果的

        mNotificationManager.notify(notifyId, mBuilder.build());
//        }
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        //蓝牙释放
        close();
        AppBus.getInstance().unregister(this);
        LogUtil.i(getResources().getString(R.string.ble_service_unbind));
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder
    {
        public BluetoothService getService()
        {
            return BluetoothService.this;
        }
    }


    //默认设备连接方法
    private int connectFailedTime ;
    private void ConnectSavedBtDevice()
    {
        if(connectFailedTime < BT_AUTO_CONNECT_MAX_TIME )
        {
            connectFailedTime++;
            LogUtil.e("第"+connectFailedTime+"次重连："+SettingSharedPreference.getDataString(this,BT_DEVICE_NAME));
            if(initialize() && !TextUtils.isEmpty(SettingSharedPreference.getDataString(this,BT_DEVICE_MAC)))
            {
                connect(SettingSharedPreference.getDataString(this,BT_DEVICE_MAC));

            }
        }
        else
        {
            //多次重连失败后

        }
    }
    /**
     * 定义订阅者
     */
    @Subscribe
    //蓝牙设备连接信息
    public void setContent(BTDeviceInfos data) {
        if(initialize())
        {
            connect(data.getBtMac());
            LogUtil.i(getResources().getString(R.string.ble_connect_start)+data.getBtMac());

        }
    }

    @Subscribe
    //蓝牙需发送信息
    public void setContent(BtSendDatas data) {
        if (data != null) {
            if (data.getType() == 0) {
                LogUtil.i("测试发送"+data.getStrData());
                sendmessage(data.getStrData());
                //测试用
                TestMsg.getInstance().AddSendNum();

            } else if (data.getType() == 1) {
                sendmessage(data.getByteDatas());
            }

        }
    }

    @Subscribe
    public void onDataChange(String sss) {
        System.out.println("====" + sss);
    }


    // Hander
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // Notify change
                    AppBus.getInstance().post(msg.obj);
                    if(msg.obj instanceof Message_DB)
                    {
                        try
                        {
                            Message_DB mTmp = (Message_DB) msg.obj;
                            if(mTmp.getMsgType() == 1)
                            {
                                Notice_DB n = new Notice_DB();
                                n.setNoticeType(0);
                                n.setNoticeTime(mTmp.getMsgTime());
                                n.setNoticeRemark(mTmp.getId()+"");
                                n.setNoticeNum(mTmp.getId()+"");
                                n.setNoticeAddress(mTmp.getRcvAddress());
                                n.setNoticeCon("收到来自【"+mTmp.getSendUserName()+"】的北斗报文");
                                n.save();
                                ShowNotice(n.getNoticeCon(),mTmp.getId());
                                AppBus.getInstance().post(n);
                            }

                        }
                        catch(Exception e)
                        {
                            LogUtil.e(e.toString());
                        }

                    }
                    else if(msg.obj instanceof BdCardBean)
                    {
//                        List<Contact_DB> cList = Contact_DB.find(Contact_DB.class,"contact_Name = ?",getResources().getString(R.string.this_device));
//                        if(cList.size() > 0)
//                        {
//                            Contact_DB cTmp = cList.get(0);
//                            cTmp.setContactName(getResources().getString(R.string.this_device));
//                            cTmp.setBdNum(BdCardBean.getInstance().getIdNum());
//                            long id = cTmp.save();
//                            AppBus.getInstance().post(cTmp);
////                            LogUtil.i(id+"");
//                        }
//                        else
//                        {
//                            Contact_DB cTmp = new Contact_DB();
//                            cTmp.setContactName(getResources().getString(R.string.this_device));
//                            cTmp.setBdNum(BdCardBean.getInstance().getIdNum());
//                            long id = cTmp.save();
//                            AppBus.getInstance().post(cTmp);
////                            LogUtil.i(id+"");
//                        }
                    }
                    break;
                case 2: // Notify change
                    Toast.makeText(BluetoothService.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;

                case 100:
                    AppBus.getInstance().post(BtConnectInfo.getInstance());
                    //5s后调用重连
                    if(!BtConnectInfo.getInstance().isConnect())
                    {
                        mHandler.sendEmptyMessageDelayed(101,5*1000);

                    }
                    break;

                case 101:
                    ConnectSavedBtDevice();
                    break;
            }
        }
    };
    private class DataAnalyseThread extends Thread
    {
        //用于控制线程开启关闭
        protected boolean isRunning;
        @Override
        public void run()
        {
            super.run();

            //完成线程内功能
            while (isRunning)
            {
                if(BtConnectInfo.getInstance().isConnect())
                {
                    if(BdCardBean.getInstance().getCardLv()<=0)
                    {
                        sendmessage(BdSdk_v2_1.BD_SendICA(0,0));
                    }
                }
            }
        }
    }
    //----------------------------蓝牙service相关----------------------------------------------------------
    /**
     * 搜索到BLE终端服务的事件
     */
    private void findService(List<BluetoothGattService> gattServices)
    {
        for (BluetoothGattService gattService : gattServices)
        {
            if(gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE.toString()))
            {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
                {
                    if(gattCharacteristic.getUuid().toString().equalsIgnoreCase(UUID_NOTIFY_READ.toString()))
                    {
                        mNotifyCharacteristic = gattCharacteristic;
                        //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                        setCharacteristicNotification(gattCharacteristic, true);
                        return;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 蓝牙初始化
     * @return 初始化成功返回true
     */
    public boolean initialize()
    {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null)
            {
                LogUtil.e(getResources().getString(R.string.btError_cant_init));
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            LogUtil.e(getResources().getString(R.string.btError_cant_get_adapter));
            return false;
        }

        return true;
    }

    /**
     *连接蓝牙
     */
    public boolean connect(String address) {
        LogUtil.i(address);
        if (mBluetoothAdapter == null || address == null) {
            LogUtil.e(getResources().getString(R.string.btError_init));
            return false;
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            LogUtil.e(getResources().getString(R.string.no_bt_device_found));
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if(mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        //mBluetoothGatt.connect();

        LogUtil.i(getResources().getString(R.string.create_new_bt_connect));

        return true;
    }

    /**
     *蓝牙连接失败
     */
    public void disconnect()
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            LogUtil.e(getResources().getString(R.string.bt_connect_failed));
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 关闭蓝牙后，释放
     */
    public void close()
    {
        BtConnectInfo.getInstance().setConnect(false);
        BdCardBean.getInstance().setCardLv(0);
        mHandler.sendEmptyMessage(100);
        if (mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtil.w(getResources().getString(R.string.btError_cant_get_adapter));
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            LogUtil.w(getResources().getString(R.string.btError_cant_get_adapter));
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices()
    {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    /**
     * 蓝牙回调
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            LogUtil.i("BtConnectionStateChange:oldStatus=" + status
                    + " NewStates=" + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
//                    LogUtil.i( "Connected to GATT server.");
                    LogUtil.i("Attempting to start service discovery:"
                            + mBluetoothGatt.discoverServices());


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                    globalData.setToastStr("蓝牙断开，请重新连接！");
                    //蓝牙断开后 清除缓存数据
//                    mBufferHandle.ClearBdData(0);
//                    globalData.setDataSourceInputType(0);
                    BtConnectInfo.getInstance().setConnect(false);
                    BdCardBean.getInstance().setCardLv(0);
                    mHandler.sendEmptyMessage(100);
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                    LogUtil.i("Disconnected from GATT server.");
                }
            }
            else
            {
                BtConnectInfo.getInstance().setConnect(false);
                BdCardBean.getInstance().setCardLv(0);
                mHandler.sendEmptyMessage(100);

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.i("onServicesDiscovered received: " + status);
                findService(gatt.getServices());
                mNotifyCharacteristic_Write = gatt.getService(UUID_SERVICE)
                        .getCharacteristic(UUID_NOTIFY_WRITE);
                // 搜索到BLE终端服务的事件
                connectFailedTime = 0;
                BtConnectInfo.getInstance().setConnect(true);
                String tmpName = gatt.getDevice().getName();
                if(tmpName == null)
                {
                    tmpName = getResources().getString(R.string.device_name);
                }
                BtConnectInfo.getInstance().setBtMac(gatt.getDevice().getAddress());
                BtConnectInfo.getInstance().setBtName(tmpName);
                mHandler.sendEmptyMessage(100);

            } else {
                if (mBluetoothGatt.getDevice().getUuids() == null)
                    LogUtil.i("onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("ok", "test--read");
            }
        }

        // //////////////////////////////////////notification对应onCharacteristicChanged///////////////
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //蓝牙底层获取数据
            byte[] dataBTRcv=characteristic.getValue();

            BufferHandle.getInstance().RawDataInput(dataBTRcv, dataBTRcv.length);

            BufferHandle.getInstance().AnalyseBuffer();
            if(BufferHandle.getInstance().AnalyseGetBDFrameCount()>0)
            {
                String iReceive= BufferHandle.getInstance().AnalyseGetBDframe();

                if(!TextUtils.isEmpty(iReceive))
                {
                    if(iReceive.contains("TXR"))
                    {
                        LogUtil.d("MSG:"+iReceive);
                    }
                    Object oTmp = BufferHandle.getInstance().sendData(iReceive);
                    if(oTmp != null)
                    {
                        if(oTmp instanceof String)
                        {
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = oTmp;
                            mHandler.sendMessage(msg);
                        }
                        else
                        {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = oTmp;
                            mHandler.sendMessage(msg);
                        }

                    }

                }
            }
//            BufferHandle.getInstance().AnalyseBuffer();

        }

        // 在此可以处理第二次发送
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.i("OnCharacteristicWrite"+status);
            if (status != BluetoothGatt.GATT_SUCCESS)
            {
                Toast.makeText(BluetoothService.this, "发送失败"+status, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor bd, int status) {
            LogUtil.i( "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor bd, int status) {
            LogUtil.i("onDescriptorWrite");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int a, int b) {
            LogUtil.i("onReadRemoteRssi");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int a) {
            LogUtil.i("onReliableWriteCompleted");
        }
    };

    /**
     * 蓝牙数据写入byte[]
     * */
    private void WriteByte(byte[] Value)
    {
        mNotifyCharacteristic_Write.setValue(Value);
        mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic_Write);
    }
    /**
     * 蓝牙数据写入String
     * */
    private void WriteValue(String Value)
    {
        mNotifyCharacteristic_Write.setValue(Value);
        mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic_Write);
    }

    /**
     * 分包发送 传入String类型
     * */
    public void sendmessage(String Str) {
        int len = Str.length();
        int count = 0;
        boolean loop = true;
        String strtmp;
        while (loop) {
            if (count * 20 < len) {
                if (len - count * 20 >= 20) {
                    strtmp = Str.substring(count * 20, count * 20 + 20);
                } else {
                    strtmp = Str.substring(count * 20, len);
                }
                try {
                    WriteValue(strtmp);
                    Thread.sleep(200);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                count += 1;
            } else
                loop = false;
        }
    }

    /**
     *分包发送 传入byte数组
     * */
    public void sendmessage(byte[] bytes) {
        ByteBuffer bftmp;
        boolean loop = true;
        int count = 0;
        int size = 0;
        int len = bytes.length;

        while (loop) {
            if (count * 20 < len) {
                if (len - count * 20 >= 20) {
                    size = 20;
                    bftmp = ByteBuffer.allocate(20);
                } else {
                    size = len - 20 * count;
                    bftmp = ByteBuffer.allocate(size);
                }

                for (int i = 20 * count; i < size + 20 * count; i++)
                    bftmp.put(bytes[i]);
                try {
                    WriteByte(bftmp.array());
                    Log.e("sendmessage", new String(bftmp.array()));
                    Thread.sleep(100);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                count += 1;
            } else
                // 发送完毕-----
                loop = false;
        }
    }
}
