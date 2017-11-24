package com.xyxl.tianyingn3.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.BtConnectInfo;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.database.Notice_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.GpsData;
import com.xyxl.tianyingn3.ui.activities.BluetoothActivity;
import com.xyxl.tianyingn3.ui.activities.ChatActivity;
import com.xyxl.tianyingn3.ui.activities.NewContactActivity;
import com.xyxl.tianyingn3.ui.activities.NewMsgActivity;
import com.xyxl.tianyingn3.ui.activities.SearchActivity;
import com.xyxl.tianyingn3.ui.activities.SetHomeBtnActivity;
import com.xyxl.tianyingn3.ui.customview.ClearEditText;
import com.xyxl.tianyingn3.ui.customview.CompassView;
import com.xyxl.tianyingn3.ui.customview.DragListAdapter;
import com.xyxl.tianyingn3.ui.customview.HomeNoticeAdapter;
import com.xyxl.tianyingn3.util.CommonUtil;
import com.xyxl.tianyingn3.util.DataUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/11/13 15:48
 * Version : V1.0
 * Introductions : 首页页面，包括搜索、信息、快捷按钮、通知中心
 */

public class HomeFragment extends BaseFragment {

    //指南针相关
    private final float MAX_ROATE_DEGREE = 1.0f;
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private LocationManager mLocationManager;
    private String mLocationProvider;
    private float mDirection;
    private float mTargetDirection;
    private AccelerateInterpolator mInterpolator;
    protected final Handler mHandler = new Handler();
    private boolean mStopDrawing;
    private boolean mChinease;

    private CompassView mPointer;
    private Bitmap imgCompass;

    //其他控件
    private ClearEditText searchHomeEdit;
    private TextView tvCardNum, tvBtConnect, tvComLv, tvBeams;
    private RelativeLayout infosBox;
    private ImageView ivOpenInfos;
    private Chronometer beamTimer;
    private LinearLayout btnBox;

    private ListView noticeList;
    private List<Notice_DB> noticeDatas;
    private HomeNoticeAdapter homeNoticeAdapter;

    private int height = 150;
    private float sDpi;

    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mPointer != null && !mStopDrawing) {
                if (mDirection != mTargetDirection) {

                    // calculate the short routine
                    float to = mTargetDirection;
                    if (to - mDirection > 180) {
                        to -= 360;
                    } else if (to - mDirection < -180) {
                        to += 360;
                    }

                    // limit the max speed to MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
                    }

                    // need to slow down if the distance is short
                    mDirection = normalizeDegree(mDirection
                            + ((to - mDirection) * mInterpolator.getInterpolation(Math
                            .abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f)));
                    mPointer.updateDirection(mDirection);
                }

//                updateDirection();

                mHandler.postDelayed(mCompassViewUpdater, 20);
            }
        }
    };

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        //指南针相关
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if (mOrientationSensor == null) {
//            getHoldingActivity().ShowToast(getResources().getString(R.string.no_sensor));

            Notice_DB n = new Notice_DB();
            n.setNoticeType(1);
            n.setNoticeTime(System.currentTimeMillis());
            n.setNoticeRemark("");
            n.setNoticeNum("");
            n.setNoticeAddress("");
            n.setNoticeCon(getResources().getString(R.string.no_sensor));
            n.save();
            AppBus.getInstance().post(n);

        }
        mDirection = 0.0f;
        mTargetDirection = 0.0f;
        mInterpolator = new AccelerateInterpolator();
        mStopDrawing = true;
        mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");

        mPointer = (CompassView) view.findViewById(R.id.compassView);
        imgCompass = BitmapFactory.decodeResource(getResources(),R.drawable.compass_cn);
        mPointer.setBitmap(imgCompass);

        mPointer.setImageResource(mChinease ? R.drawable.compass_cn : R.drawable.compass);

        //其他控件
        searchHomeEdit = (ClearEditText)view.findViewById(R.id.searchHome);
        tvBtConnect = (TextView)view.findViewById(R.id.textBtConnect);
        tvCardNum = (TextView)view.findViewById(R.id.textCardNum);
        tvComLv = (TextView)view.findViewById(R.id.textComLv);
        tvBeams = (TextView)view.findViewById(R.id.textbeams);
        infosBox = (RelativeLayout)view.findViewById(R.id.infosBox);
        noticeList = (ListView)view.findViewById(R.id.homeList);
        ivOpenInfos = (ImageView)view.findViewById(R.id.ivOpenInfos);
        beamTimer = (Chronometer)view.findViewById(R.id.beamTimer);
        btnBox = (LinearLayout) view.findViewById(R.id.btnBox);
        RefreshBtnBox();

        beamTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                tvBeams.setText("");
                int[] beams = BdCardBean.getInstance().getBeamLvs();
                if(beams == null)
                {
                    beams = new int[10];
                }
                for(int i=0;i<10;i++)
                {
                    tvBeams.append(" "+beams[i]+" ");
                }

                //
                tvBeams.setText(
                        MyPosition.getInstance().getMyLon()+":"+MyPosition.getInstance().getMyLat()+"\n"+
                        DataUtil.byte2HexStr(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon()),1)
                        +":"+DataUtil.byte2HexStr(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat()),1)+"\n"+
                        new String(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon()))+":"+
                                new String(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat())));
            }
        });

        ivOpenInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GetBoxHeight())
                {
                    while(height != 250)
                    {
                        height += 100;
                        infosBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * sDpi)));
                        beamTimer.start();
                    }
                }
                else
                {
                    while(height != 150)
                    {
                        height -= 100;
                        infosBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * sDpi)));
                        beamTimer.stop();
                    }
                }


            }
        });

        searchHomeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false, SearchActivity.class);
            }
        });

        //通知事件
        noticeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notice_DB tmpN = noticeDatas.get(position);
                if(tmpN.getNoticeType() == 0)
                {
                    long _id = CommonUtil.Str2long(tmpN.getNoticeRemark());
                    if(_id >= 0)
                    {
                        Message_DB msg = Message_DB.findById(Message_DB.class,_id);
                        if(msg != null)
                        {
                            Intent intent = new Intent(getActivity(),ChatActivity.class);
                            intent.putExtra("msg",msg);
                            startActivity(intent);
                        }
                        else
                        {
                            getHoldingActivity().ShowToast(getResources().getString(R.string.msg_miss));
                            tmpN.delete();
                            noticeDatas.remove(position);
                            RefreshUI();
                        }
                    }
                }
                else if(tmpN.getNoticeType() == 1)
                {
                    if(getResources().getString(R.string.permission_denied).equals(tmpN.getNoticeCon()))
                    {
                        requestPosPermission();
                        tmpN.delete();
                        noticeDatas.remove(position);
                        RefreshUI();
                    }
                }
            }
        });

        RefreshUI();


    }

    private void requestPosPermission() {
        if (ContextCompat.checkSelfPermission(getHoldingActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getHoldingActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getHoldingActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getHoldingActivity(),
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

            RefreshUI();
        }
    }

    private void RefreshBtnBox() {
        btnBox.removeAllViews();
        String homeBtns = SettingSharedPreference.getDataString(getHoldingActivity(),HOME_BTNS_FLAG);

        if(TextUtils.isEmpty(homeBtns))
        {
            homeBtns = "";
            for(int i=0;i<HOME_BTNS_INFOS.length;i++)
            {
                homeBtns += HOME_BTNS_INFOS[i]+"f"+1+"g";
            }
        }

        try {
            String[] homePerBtn = homeBtns.split("g");
            List<Integer> flags = new ArrayList<Integer>();
            for (int i = 0; i < homeBtns.length(); i++) {
                String[] tmp = homePerBtn[i].split("f");
                if (tmp[1].equals("1")) {
                    Button btn = new Button(getHoldingActivity());
                    btn.setText(tmp[0]);
                    btn.setTag(tmp[0]);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BtnOpenActivity((String)v.getTag());
//                            getHoldingActivity().OpenActivity(false, SetHomeBtnActivity.class);
                        }


                    });
                    btnBox.addView(btn);
                }
            }

        } catch (Exception e) {

        }

        Button btnNew = new Button(getHoldingActivity());
        btnNew.setText("设置");
        btnBox.addView(btnNew);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false, SetHomeBtnActivity.class);
            }
        });

    }
    private void BtnOpenActivity(String tag) {
        if(tag.equals(HOME_BTNS_INFOS[0]))
        {
            if(BtConnectInfo.getInstance().isConnect() || !TextUtils.isEmpty(BdCardBean.getInstance().getIdNum()))
            {
                getHoldingActivity().OpenActivity(false, NewMsgActivity.class);
            }
            else
            {
                getHoldingActivity().ShowToast(getResources().getString(R.string.cant_send_msg));
            }

        }
        else if(tag.equals(HOME_BTNS_INFOS[1]))
        {
            getHoldingActivity().OpenActivity(false, NewContactActivity.class);
        }
        else if(tag.equals(HOME_BTNS_INFOS[2]))
        {
            getHoldingActivity().OpenActivity(false, BluetoothActivity.class);
        }
        else if(tag.equals(HOME_BTNS_INFOS[3]))
        {
//            getHoldingActivity().OpenActivity(false, NewContactActivity.class);
        }
        else if(tag.equals(HOME_BTNS_INFOS[4]))
        {
//            getHoldingActivity().OpenActivity(false, BluetoothActivity.class);
        }
    }

    private boolean GetBoxHeight()
    {
        sDpi = getHoldingActivity().getScreenDpi();
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) infosBox.getLayoutParams();
        height = (int) (linearParams.height / sDpi);
        LogUtil.i(height+"DP");
        if(height == 150)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void RefreshUI()
    {
        if(BtConnectInfo.getInstance().isConnect())
        {
            tvBtConnect.setText(getResources().getString(R.string.connected_to)+BtConnectInfo.getInstance().getBtName());
        }
        else
        {
            tvBtConnect.setText(getResources().getString(R.string.connected_no));
        }
        
        if(TextUtils.isEmpty(BdCardBean.getInstance().getIdNum()))
        {
            tvCardNum.setText(getResources().getString(R.string.bd_card_num)+getResources().getString(R.string.no_bd_card_num));
        }
        else
        {
            tvCardNum.setText(getResources().getString(R.string.bd_card_num)+BdCardBean.getInstance().getIdNum());
        }

        tvComLv.setText(getResources().getString(R.string.bd_com_lv)+BdCardBean.getInstance().getCardLv());

        tvBeams.setText("");
        int[] beams = BdCardBean.getInstance().getBeamLvs();
        if(beams == null)
        {
            beams = new int[10];
        }
        for(int i=0;i<10;i++)
        {
            tvBeams.append(" "+beams[i]+" ");
        }

        noticeDatas = Notice_DB.listAll(Notice_DB.class,"notice_Time desc");
        homeNoticeAdapter = new HomeNoticeAdapter(getHoldingActivity(),noticeDatas);
        noticeList.setAdapter(homeNoticeAdapter);

    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mLocationProvider != null) {
//            updateLocation(mLocationManager.getLastKnownLocation(mLocationProvider));
//            mLocationManager.requestLocationUpdates(mLocationProvider, 2000, 10, mLocationListener);
//        } else {
////            mLocationTextView.setText(R.string.cannot_get_location);
//        }
        if (mOrientationSensor != null) {
            mSensorManager.registerListener(mOrientationSensorEventListener, mOrientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        mStopDrawing = false;
        mHandler.postDelayed(mCompassViewUpdater, 20);

        RefreshBtnBox();
        RefreshUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        mStopDrawing = true;
        if (mOrientationSensor != null) {
            mSensorManager.unregisterListener(mOrientationSensorEventListener);
        }

    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if(imgCompass!=null && !imgCompass.isRecycled())
        {
            imgCompass.recycle();
        }
        super.onDestroy();

    }

    private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float direction = event.values[0] * -1.0f;
            mTargetDirection = normalizeDegree(direction);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }

    @Subscribe
    public void setContent(BdCardBean data) {
        RefreshUI();
    }

    @Subscribe
    public void setContent(Notice_DB data) {
//        if(data.getMsgType() == 1)
//        {
            LogUtil.e("rcvMSG");

            RefreshUI();
//        }
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
