package com.xyxl.tianyingn3.ui.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bluetooth.BTDeviceInfos;
import com.xyxl.tianyingn3.bluetooth.BluetoothService;
import com.xyxl.tianyingn3.bluetooth.BtSendDatas;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.database.Msg_DB;
import com.xyxl.tianyingn3.database.NewMsgCount_DB;
import com.xyxl.tianyingn3.database.Notice_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.service.MainService;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;
import com.xyxl.tianyingn3.ui.fragments.BaseFragment;
import com.xyxl.tianyingn3.ui.fragments.HomeFragment;
import com.xyxl.tianyingn3.ui.fragments.MapFragment;
import com.xyxl.tianyingn3.ui.fragments.MessageFragment;
import com.xyxl.tianyingn3.ui.fragments.SettingFragment;
import com.xyxl.tianyingn3.util.DataUtil;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2017/11/13 15:30
 * Version : V1.0
 * Introductions : 主要页面，分布各个功能主页面
 */
public class MainActivity extends BaseActivity {

    //UI控件
    private TextView homeText;
    private ImageView homeImg;
    private RelativeLayout homeBox;
    private TextView msgText;
    private ImageView msgImg;
    private RelativeLayout msgBox;
    private ImageView sosImgBtn;
    private RelativeLayout sosBox;
    private TextView mapText;
    private ImageView mapImg;
    private RelativeLayout mapBox;
    private TextView setText;
    private ImageView setImg;
    private RelativeLayout setBox;
    private RelativeLayout buttomNaviBox;
    private FrameLayout fragmentsBox;
    private TextView tvNewMsg;
    //Fragments
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private MapFragment mapFragment;
    private SettingFragment settingFragment;
    //fragment tab页
    private BaseFragment[] fragments;
    private FragmentTransaction trx;
    // 当前fragment的index
    private int currentTabIndex;

    //蓝牙服务相关
    private BluetoothService myService;

    //UI界面相关参数
    private float sDpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setExitFlag(1);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        initListener();

        ShowNewMsgNum();

        //获取权限
        if (isMarshmallow()) {
            requestPosPermission();//然后在回调中处理
        }
    }

    private boolean isMarshmallow()
    {
        return Build.VERSION.SDK_INT >= 23;
    }

    private void requestPosPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                    3);
        }
    }

    private static final int REQUEST_PERMISSION_CAMERA_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            LogUtil.i("onRequestPermissionsResult granted=" + granted);
            if(!granted)
            {
                Notice_DB n = new Notice_DB();
                n.setNoticeType(1);
                n.setNoticeTime(System.currentTimeMillis());
                n.setNoticeRemark("");
                n.setNoticeNum("");
                n.setNoticeAddress("");
                n.setNoticeCon(getResources().getString(R.string.permission_denied));
                n.save();
                AppBus.getInstance().post(n);
            }
        }
    }
    private void initData() {



        //初始化Fragments
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        mapFragment = new MapFragment();
        settingFragment = new SettingFragment();

        fragments = new BaseFragment[] {homeFragment, messageFragment, mapFragment, settingFragment};

        // 添加显示第一个fragment
        getFragmentManager().beginTransaction().add(R.id.fragmentsBox, homeFragment)
                .add(R.id.fragmentsBox, messageFragment)
                .add(R.id.fragmentsBox, mapFragment)
                .add(R.id.fragmentsBox, settingFragment)
                .hide(messageFragment).hide(mapFragment).hide(settingFragment).show(homeFragment).commit();
        currentTabIndex = 0;

        //开启主服务
        Intent mainIntent = new Intent(this, MainService.class);
        startService(mainIntent);

        //开启蓝牙服务
        Intent gattServiceIntent = new Intent(this, BluetoothService.class);
        boolean m = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        if(!m)
        {
            LogUtil.e( getResources().getString(R.string.bt_service_open_failed) );
        }
    }

    private void initView() {
        homeText = (TextView) findViewById(R.id.homeText);
        homeImg = (ImageView) findViewById(R.id.homeImg);
        homeBox = (RelativeLayout) findViewById(R.id.homeBox);
        msgText = (TextView) findViewById(R.id.msgText);
        msgImg = (ImageView) findViewById(R.id.msgImg);
        msgBox = (RelativeLayout) findViewById(R.id.msgBox);
        sosImgBtn = (ImageView) findViewById(R.id.sosImgBtn);
        sosBox = (RelativeLayout) findViewById(R.id.sosBox);
        mapText = (TextView) findViewById(R.id.mapText);
        mapImg = (ImageView) findViewById(R.id.mapImg);
        mapBox = (RelativeLayout) findViewById(R.id.mapBox);
        setText = (TextView) findViewById(R.id.setText);
        setImg = (ImageView) findViewById(R.id.setImg);
        setBox = (RelativeLayout) findViewById(R.id.setBox);
        buttomNaviBox = (RelativeLayout) findViewById(R.id.buttomNaviBox);
        fragmentsBox = (FrameLayout) findViewById(R.id.fragmentsBox);
        tvNewMsg = (TextView) findViewById(R.id.textNewMsg);
        tvNewMsg.setVisibility(View.INVISIBLE);
        //控件设置
        sDpi = getScreenDpi();

        homeText.setTextColor(getResources().getColor(R.color.choose_text_color));
        homeText.setTextSize(10);
        msgText.setTextColor(getResources().getColor(R.color.white));
        msgText.setTextSize(10);
        mapText.setTextColor(getResources().getColor(R.color.white));
        mapText.setTextSize(10);
        setText.setTextColor(getResources().getColor(R.color.white));
        setText.setTextSize(10);
        homeImg.setSelected(true);
        msgImg.setSelected(false);
        mapImg.setSelected(false);
        setImg.setSelected(false);

        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getMyDpHeight(48));
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttomNaviBox.setLayoutParams(relativeParams);

        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(getMyDpWidth(23.5f), getMyDpHeight(22f));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btnParams.setMargins(0,getMyDpHeight(6),0,getMyDpHeight(5.5f));
        homeImg.setLayoutParams(btnParams);
        btnParams = new RelativeLayout.LayoutParams(getMyDpWidth(24f), getMyDpHeight(24f));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btnParams.setMargins(0,getMyDpHeight(5),0,getMyDpHeight(4f));

        msgImg.setLayoutParams(btnParams);
        btnParams = new RelativeLayout.LayoutParams(getMyDpWidth(18.5f), getMyDpHeight(23.5f));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btnParams.setMargins(0,getMyDpHeight(5),0,getMyDpHeight(4.5f));
        mapImg.setLayoutParams(btnParams);
        btnParams = new RelativeLayout.LayoutParams(getMyDpWidth(20f), getMyDpHeight(24f));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btnParams.setMargins(0,getMyDpHeight(5),0,getMyDpHeight(3.5f));
        setImg.setLayoutParams(btnParams);

        btnParams = new RelativeLayout.LayoutParams(getMyDpWidth(58.5f), getMyDpHeight(58.5f));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btnParams.setMargins(0,0,0,getMyDpHeight(6.5f));
        sosImgBtn.setLayoutParams(btnParams);


    }



    private void initListener() {
        //导航栏部分
        homeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeText.setTextColor(getResources().getColor(R.color.choose_text_color));
                msgText.setTextColor(getResources().getColor(R.color.white));
                mapText.setTextColor(getResources().getColor(R.color.white));
                setText.setTextColor(getResources().getColor(R.color.white));
                Turn2Fragment(0);
                homeImg.setSelected(true);
                msgImg.setSelected(false);
                mapImg.setSelected(false);
                setImg.setSelected(false);
//                AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendICA(0, 0),null));


                try
                {
                    Msg_DB m = new Msg_DB();
                    m.setAddress("11");
                    m.setMsgId(11);
                    m.save();
                }
                catch(Error e)
                {
                    Log.e("ERROR",e.toString()+"");
                }



            }
        });
        msgBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgText.setTextColor(getResources().getColor(R.color.choose_text_color));
                homeText.setTextColor(getResources().getColor(R.color.white));
                mapText.setTextColor(getResources().getColor(R.color.white));
                setText.setTextColor(getResources().getColor(R.color.white));
                Turn2Fragment(1);

                homeImg.setSelected(false);
                msgImg.setSelected(true);
                mapImg.setSelected(false);
                setImg.setSelected(false);

            }
        });
        mapBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapText.setTextColor(getResources().getColor(R.color.choose_text_color));
                homeText.setTextColor(getResources().getColor(R.color.white));
                msgText.setTextColor(getResources().getColor(R.color.white));
                setText.setTextColor(getResources().getColor(R.color.white));
                Turn2Fragment(2);

                homeImg.setSelected(false);
                msgImg.setSelected(false);
                mapImg.setSelected(true);
                setImg.setSelected(false);
            }
        });
        setBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText.setTextColor(getResources().getColor(R.color.choose_text_color));
                homeText.setTextColor(getResources().getColor(R.color.white));
                mapText.setTextColor(getResources().getColor(R.color.white));
                msgText.setTextColor(getResources().getColor(R.color.white));
                Turn2Fragment(3);

                homeImg.setSelected(false);
                msgImg.setSelected(false);
                mapImg.setSelected(false);
                setImg.setSelected(true);
            }
        });

        //SOS中间按钮
        sosBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenActivity(false,SosActivity.class);
            }
        });
    }

    //更新Fragment页面
    private void Turn2Fragment(int index)
    {
        if (currentTabIndex != index) {
            FragmentTransaction trx = getFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragmentsBox, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

    // service连接
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            myService = ((BluetoothService.LocalBinder) service).getService();
            if (!myService.initialize())
            {
                finish();
            }
            myService.connect(SettingSharedPreference.getDataString(MainActivity.this,BT_DEVICE_MAC));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if(myService!=null)
            {
                myService.disconnect();//----------------------放弃连接
            }
            myService = null;
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        unbindService(mServiceConnection);

		Intent mIntent=new Intent(MainActivity.this,MainService.class);
        stopService(mIntent);

        super.onDestroy();
    }

    private void ShowNewMsgNum()
    {
        List<NewMsgCount_DB> listNew = NewMsgCount_DB.listAll(NewMsgCount_DB.class);
        int num = 0;
        for(int i=0;i<listNew.size();i++)
        {
            num+=listNew.get(i).getNum();
        }
        if(num>0)
        {
            tvNewMsg.setVisibility(View.VISIBLE);
            tvNewMsg.setText(num+"");
        }
        else
        {
            tvNewMsg.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 定义订阅者
     */
    @Subscribe
    public void setContent(NewMsgCount_DB data) {
//        ShowToast("rcv_msg");
        ShowNewMsgNum();
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册到bus事件总线中
        AppBus.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppBus.getInstance().unregister(this);
    }
}
