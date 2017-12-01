package com.xyxl.tianyingn3.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BtConnectInfo;
import com.xyxl.tianyingn3.bluetooth.BTDeviceInfos;
import com.xyxl.tianyingn3.bluetooth.BtSendDatas;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.global.TestMsg;
import com.xyxl.tianyingn3.logs.LogUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/13 15:30
 * Version : V1.0
 * Introductions : 蓝牙搜索、连接页面
 */
public class BluetoothActivity extends BaseActivity implements View.OnClickListener {

    //控件
    private Switch switchBt;
    private TextView textBtOnOff;
    private RelativeLayout switchbox;
    private ListView btDeviceList;

    private TextView tvDeviceName, tvDeviceMac;

    //
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private boolean mScanning = false; // 扫描标志
    private static final long SCAN_PERIOD = 10000;// 10秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initBt();

        initView();

    }

    private void initBt() {
        /* 判断设备是否支持BLE feature, false时判断，不支持的设备也可以安装使用 */
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            ShowToast(getResources().getString(R.string.ble_cant_use));
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 打开蓝牙对话框
        if (mBluetoothAdapter == null || mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    private void initView() {
        switchBt = (Switch) findViewById(R.id.switchBt);
        textBtOnOff = (TextView) findViewById(R.id.textBtOnOff);
        tvDeviceName = (TextView) findViewById(R.id.device_name);
        tvDeviceMac = (TextView) findViewById(R.id.device_address);
        switchbox = (RelativeLayout) findViewById(R.id.switchbox);
        btDeviceList = (ListView) findViewById(R.id.btDeviceList);

        if(BtConnectInfo.getInstance().isConnect())
        {
            tvDeviceName.setText(BtConnectInfo.getInstance().getBtName());
            tvDeviceMac.setText(BtConnectInfo.getInstance().getBtMac());
        }

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        btDeviceList.setAdapter(mLeDeviceListAdapter);
        btDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                final BluetoothDevice device = mLeDeviceListAdapter
                        .getDevice(position);
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(
                        BluetoothActivity.this);
                builder.setTitle("是否连接设备：" + device.getName());
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
//								final Intent intent = new Intent(
//										BleSearchActivity.this,
//										MainActivity.class);
//                                CacheActivity.finishActivity();
//                                final Intent intent = new Intent(
//                                        BleSearchActivity.this,
//
//                                        MainBaiduActivity.class);

//								final Intent intent = new Intent(
//										BleSearchActivity.this,
//										MainBaiduActivity.class);


                                if (device == null)
                                {
                                    return;

                                }
                                else
                                {
                                    AppBus.getInstance().post(new BTDeviceInfos(device.getName(), device.getAddress(), 0));
                                    ShowToast(getResources().getString(R.string.connectting));
                                    SettingSharedPreference.setDataString(BluetoothActivity.this,BT_DEVICE_NAME,device.getName());
                                    SettingSharedPreference.setDataString(BluetoothActivity.this,BT_DEVICE_MAC,device.getAddress());
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean running = true;
                                            long startTime = System.currentTimeMillis();
                                            int passTime = 0;
                                            while(running)
                                            {
                                                passTime = (int) (System.currentTimeMillis() - startTime);
                                                if(BtConnectInfo.getInstance().isConnect())
                                                {
                                                    running = false;
                                                    mHandler.sendEmptyMessage(100);
                                                }
                                                else if(passTime > 15*1000)
                                                {
                                                    running = false;
                                                    ShowToast(getResources().getString(R.string.connected_failed));
                                                }
                                            }
                                        }
                                    }).start();
                                }
                                // intent.putExtra(BleSearchActivity.EXTRAS_DEVICE_NAME,
                                // device.getName());
                                // intent.putExtra(BleSearchActivity.EXTRAS_DEVICE_ADDRESS,
                                // device.getAddress());
//                                globalData.setDeviceAddress(device.getAddress());
//                                mLeDeviceListAdapter.clear();
//                                btDeviceList.setAdapter(mLeDeviceListAdapter);
//                                if (mScanning) {
//                                    mBluetoothAdapter
//                                            .stopLeScan(mLeScanCallback);
//                                    btscan.setText(" 开始搜索");
//                                    mScanning = false;
//                                }
//                                startActivity(intent);
//                                BluetoothActivity.this.finish();
                            }

                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                            }

                        });
                builder.create().show();
            }

        });

        switchBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBluetoothAdapter.enable();
                    scanLeDevice(true);
                } else {
                    mBluetoothAdapter.disable();

                }
            }
        });

        if (!mBluetoothAdapter.isEnabled()) {
            switchBt.setChecked(false);

        } else {
            switchBt.setChecked(true);
            scanLeDevice(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btnBtSearch:
//                scanLeDevice(true);
//                break;
        }
    }

    /**
     * LeDeviceListAdapter类 完成搜索到的蓝牙设备列表操作
     * */
    private class LeDeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = BluetoothActivity.this.getLayoutInflater();
        }

        // 添加蓝牙设备
        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        // 移除所有设备
        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mLeDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.devicelist, null);
                viewHolder.deviceName = (TextView) (convertView
                        .findViewById(R.id.device_name));
                viewHolder.deviceAddress = (TextView) (convertView
                        .findViewById(R.id.device_address));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // 显示设备名称
            BluetoothDevice device = mLeDevices.get(position);
            if (device.getName() != null && device.getName().length() > 0)
                viewHolder.deviceName.setText(device.getName());
            else
                viewHolder.deviceName.setText("未知的设备");
            viewHolder.deviceAddress.setText(device.getAddress());

            return convertView;
        }

    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mLeDeviceListAdapter.addDevice(device);
//                    mHandler.sendEmptyMessage(1);
//                }
//            });

            if (Looper.myLooper() == Looper.getMainLooper()) {
                // Android 5.0 及以上
                mLeDeviceListAdapter.addDevice(device);
                mHandler.sendEmptyMessage(1);
            } else {
                // Android 5.0 以下
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLeDeviceListAdapter.addDevice(device);
                        mHandler.sendEmptyMessage(1);
                    }
                });
            }
        }
    };

    // Hander
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // Notify change
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    break;
                case 100: //
                    finish();
                    break;
            }
        }
    };

    // 扫描Ble设备
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            // Causes the Runnable r to be added to the message
            // queue, to be run after the specified amount of time elapses.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mScanning) {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        invalidateOptionsMenu();// 更新optionMenu
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            // F000E0FF-0451-4000-B000-000000000000
            mLeDeviceListAdapter.clear();
            mHandler.sendEmptyMessage(1);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!BtConnectInfo.getInstance().isConnect())
            {
                showExitAlert();

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitAlert() {
        // TODO 自动生成的方法存根
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.donot_connect_bd))
                // .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作

                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.esc), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作

                    }
                }).show();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        AppBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//        scanLeDevice(false);
        AppBus.getInstance().unregister(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        scanLeDevice(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED)
        {
            finish();
            return;
        }
    }

    @Subscribe
    //蓝牙需发送信息
    public void setContent(BtConnectInfo data) {
        if (data != null) {
            if(BtConnectInfo.getInstance().isConnect())
            {
                tvDeviceName.setText(BtConnectInfo.getInstance().getBtName());
                tvDeviceMac.setText(BtConnectInfo.getInstance().getBtMac());
            }
            else
            {
                tvDeviceName.setText("");
                tvDeviceMac.setText("");

            }
        }
    }

}
