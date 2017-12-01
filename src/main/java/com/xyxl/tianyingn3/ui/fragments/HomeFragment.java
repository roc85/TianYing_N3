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
import android.util.LayoutDirection;
import android.view.Gravity;
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
import android.widget.GridLayout;
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
import com.xyxl.tianyingn3.global.App;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.MyApp;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.GpsData;
import com.xyxl.tianyingn3.ui.activities.BluetoothActivity;
import com.xyxl.tianyingn3.ui.activities.ChatActivity;
import com.xyxl.tianyingn3.ui.activities.NewContactActivity;
import com.xyxl.tianyingn3.ui.activities.NewMsgActivity;
import com.xyxl.tianyingn3.ui.activities.SearchActivity;
import com.xyxl.tianyingn3.ui.activities.SetHomeBtnActivity;
import com.xyxl.tianyingn3.ui.customview.BeamsView;
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
    private RelativeLayout infosBox, addInfoBox;
    private ImageView ivOpenInfos;
    private Chronometer beamTimer;
    private LinearLayout btnBox;
    private TextView tvTitle;
    private TextView tvPower, tvRunTime;
    private ImageView ivLeft, ivRight;
    private BeamsView beamsView;
    private GridLayout fastGroup;

    private ListView noticeList;
    private List<Notice_DB> noticeDatas;
    private HomeNoticeAdapter homeNoticeAdapter;

    private int height = 150;
    private float sDpi;

    private boolean needFreshBtns = true;

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
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

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
        imgCompass = BitmapFactory.decodeResource(getResources(),R.drawable.homepage_compass);
        mPointer.setBitmap(imgCompass);


        //其他控件
//        searchHomeEdit = (ClearEditText)view.findViewById(R.id.searchHome);
        tvBtConnect = (TextView)view.findViewById(R.id.textBtConnect);
        tvCardNum = (TextView)view.findViewById(R.id.textCardNum);
        tvComLv = (TextView)view.findViewById(R.id.textComLv);
        tvBeams = (TextView)view.findViewById(R.id.textbeams);
        tvTitle = (TextView)view.findViewById(R.id.textHomeTitle);
        tvPower = (TextView)view.findViewById(R.id.powerInfo);
        tvRunTime = (TextView)view.findViewById(R.id.timeInfo);
        infosBox = (RelativeLayout)view.findViewById(R.id.infosBox);
        addInfoBox = (RelativeLayout)view.findViewById(R.id.addInfoBox);
        noticeList = (ListView)view.findViewById(R.id.homeList);
        ivOpenInfos = (ImageView)view.findViewById(R.id.ivOpenInfos);
        ivLeft = (ImageView)view.findViewById(R.id.imageHomeLeft);
        ivRight = (ImageView)view.findViewById(R.id.imageHomeRight);
        beamTimer = (Chronometer)view.findViewById(R.id.beamTimer);
        btnBox = (LinearLayout) view.findViewById(R.id.btnBox);
        beamsView = (BeamsView)view.findViewById(R.id.beamView);
        fastGroup = (GridLayout)view.findViewById(R.id.fastBtnGroup);

        RefreshBtnBox();
        beamTimer.start();
        beamTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
//                tvBeams.setText("");
                int[] beams = BdCardBean.getInstance().getBeamLvs();
                if(beams == null)
                {
                    beams = new int[10];
                }
                beamsView.updateDirection(beams);
                int time = (int) ((System.currentTimeMillis() - App.getStartTime())/1000/60);
                tvRunTime.setText((time>59?(time/ 60 + "h" + time % 60):time)+"m");
//                tvPower.setText("");

//                for(int i=0;i<beams.length;i++)
//                {
//                    tvBeams.append(" "+beams[i]+" ");
//                }

                //
//                tvBeams.setText(
//                        MyPosition.getInstance().getMyLon()+":"+MyPosition.getInstance().getMyLat()+"\n"+
//                        DataUtil.byte2HexStr(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon()),1)
//                        +":"+DataUtil.byte2HexStr(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat()),1)+"\n"+
//                        new String(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon()))+":"+
//                                new String(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat())));
            }
        });

        ivOpenInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(addInfoBox.getVisibility() == View.GONE)
            {
                addInfoBox.setVisibility(View.VISIBLE);
                addInfoBox.startAnimation(AnimationUtils.loadAnimation(getHoldingActivity(),R.anim.add_infos_show));
                infosBox.setBackgroundResource(R.drawable.home_round_rect_up);
                ivOpenInfos.setImageResource(R.drawable.homepage_button_detail_icon_arrow);
            }
            else if(addInfoBox.getVisibility() == View.VISIBLE)
            {
                Animation animation = AnimationUtils.loadAnimation(getHoldingActivity(),R.anim.add_infos_hide);
                addInfoBox.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        addInfoBox.setVisibility(View.GONE);
                        infosBox.setBackgroundResource(R.drawable.home_round_rect_back);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
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

        InitUi();

        noticeDatas = Notice_DB.listAll(Notice_DB.class,"notice_Time desc");
        homeNoticeAdapter = new HomeNoticeAdapter(getHoldingActivity(),noticeDatas);
        noticeList.setAdapter(homeNoticeAdapter);
    }

    private void InitUi() {

        addInfoBox.setVisibility(View.GONE);

        tvTitle.setTextSize(18);
        tvBtConnect.setTextSize(15);
        tvCardNum.setTextSize(15);
        tvComLv.setTextSize(15);

        RelativeLayout.LayoutParams relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(22.5f), getHoldingActivity().getMyDpHeight(28f));
        relaParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParams.setMargins(getHoldingActivity().getMyDpHeight(13),getHoldingActivity().getMyDpHeight(15),0,getHoldingActivity().getMyDpHeight(5f));
        ivLeft.setLayoutParams(relaParams);

        relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(23f), getHoldingActivity().getMyDpHeight(23f));
        relaParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParams.setMargins(getHoldingActivity().getMyDpHeight(0),getHoldingActivity().getMyDpHeight(0),getHoldingActivity().getMyDpHeight(17.5f),getHoldingActivity().getMyDpHeight(5f));
        ivRight.setLayoutParams(relaParams);

        LinearLayout.LayoutParams lineParams =
                new LinearLayout.LayoutParams(getHoldingActivity().getMyDpWidth(338f), getHoldingActivity().getMyDpHeight(147f));
        lineParams.setMargins(getHoldingActivity().getMyDpWidth(11),
                getHoldingActivity().getMyDpHeight(0),
                getHoldingActivity().getMyDpWidth(11),
                getHoldingActivity().getMyDpHeight(0));

        infosBox.setLayoutParams(lineParams);

        lineParams =
                new LinearLayout.LayoutParams(getHoldingActivity().getMyDpWidth(338f), ViewGroup.LayoutParams.WRAP_CONTENT);
        lineParams.setMargins(getHoldingActivity().getMyDpWidth(11),
                getHoldingActivity().getMyDpHeight(0),
                getHoldingActivity().getMyDpWidth(11),
                getHoldingActivity().getMyDpHeight(0));
        addInfoBox.setLayoutParams(lineParams);

        relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(160f), getHoldingActivity().getMyDpHeight(55f));
        relaParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relaParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        beamsView.setLayoutParams(relaParams);


        relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(80f), getHoldingActivity().getMyDpHeight(80f));
        relaParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParams.setMargins(getHoldingActivity().getMyDpHeight(25),getHoldingActivity().getMyDpHeight(0),getHoldingActivity().getMyDpHeight(0),getHoldingActivity().getMyDpHeight(0));
        mPointer.setLayoutParams(relaParams);
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
        if (needFreshBtns) {
            fastGroup.removeAllViews();
            String homeBtns = SettingSharedPreference.getDataString(getHoldingActivity(), HOME_BTNS_FLAG);

            if (TextUtils.isEmpty(homeBtns)) {
                homeBtns = "";
                for (int i = 0; i < HOME_BTNS_INFOS.length; i++) {
                    homeBtns += HOME_BTNS_INFOS[i] + "f" + 1 + "g";
                }
            }

            try {
                String[] homePerBtn = homeBtns.split("g");
                List<Integer> flags = new ArrayList<Integer>();
                for (int i = 0; i < homeBtns.length(); i++) {
                    String[] tmp = homePerBtn[i].split("f");
                    if (tmp[1].equals("1")) {
                        LinearLayout btnBoxItem = new LinearLayout(getHoldingActivity());
                        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(getHoldingActivity().getMyDpWidth(90f), ViewGroup.LayoutParams.WRAP_CONTENT);

                        btnBoxItem.setLayoutParams(lineParams);
                        btnBoxItem.setOrientation(LinearLayout.VERTICAL);
                        btnBoxItem.setTag(tmp[0]);
                        ImageView ivBtn = new ImageView(getHoldingActivity());
                        lineParams =
                                new LinearLayout.LayoutParams(getHoldingActivity().getMyDpWidth(34f), getHoldingActivity().getMyDpHeight(34f));
                        lineParams.setMargins(0, getHoldingActivity().getMyDpHeight(6f), 0,getHoldingActivity().getMyDpHeight(6f));
                        lineParams.gravity = Gravity.CENTER;
                        ivBtn.setLayoutParams(lineParams);

                        ivBtn.setBackgroundResource(getImageRes(tmp[0]));

                        lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lineParams.gravity = Gravity.CENTER;
                        TextView tvBtn = new TextView(getHoldingActivity());
                        tvBtn.setLayoutParams(lineParams);
                        tvBtn.setTextColor(0xFFFFFFFF);
                        tvBtn.setTextSize(10);
                        tvBtn.setText(tmp[0]);

                        btnBoxItem.addView(ivBtn);
                        btnBoxItem.addView(tvBtn);

                        btnBoxItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BtnOpenActivity((String) v.getTag());
                            }


                        });
                        fastGroup.addView(btnBoxItem);
                    }
                }

            } catch (Exception e) {

            }

            LinearLayout btnBoxItemSet = new LinearLayout(getHoldingActivity());
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(getHoldingActivity().getMyDpWidth(90f), ViewGroup.LayoutParams.WRAP_CONTENT);
            btnBoxItemSet.setLayoutParams(lineParams);
            btnBoxItemSet.setOrientation(LinearLayout.VERTICAL);
            btnBoxItemSet.setTag("设置");
            ImageView ivBtn = new ImageView(getHoldingActivity());
            lineParams =
                    new LinearLayout.LayoutParams(getHoldingActivity().getMyDpWidth(34f), getHoldingActivity().getMyDpHeight(34f));
            lineParams.setMargins(0, getHoldingActivity().getMyDpHeight(6f), 0,getHoldingActivity().getMyDpHeight(6f));
            lineParams.gravity = Gravity.CENTER;
            ivBtn.setLayoutParams(lineParams);
            ivBtn.setBackgroundResource(R.drawable.homepage_icon_set);

            lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lineParams.gravity = Gravity.CENTER;
            TextView tvBtn = new TextView(getHoldingActivity());
            tvBtn.setLayoutParams(lineParams);
            tvBtn.setTextColor(0xFFFFFFFF);
            tvBtn.setTextSize(10);
            tvBtn.setText("设置");

            btnBoxItemSet.addView(ivBtn);
            btnBoxItemSet.addView(tvBtn);
//            Button btnNew = new Button(getHoldingActivity());
//            btnNew.setText("设置");
            fastGroup.addView(btnBoxItemSet);
            btnBoxItemSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getHoldingActivity().OpenActivity(false, SetHomeBtnActivity.class);
                }
            });
        }
//        needFreshBtns = false;


    }

    private int getImageRes(String tag) {
        int res = R.mipmap.ic_launcher_round;
        if(tag.equals(HOME_BTNS_INFOS[0]))
        {
            res = R.drawable.homepage_icon_massage;

        }
        else if(tag.equals(HOME_BTNS_INFOS[1]))
        {
            res = R.drawable.homepage_icon_create_contact;
        }
        else if(tag.equals(HOME_BTNS_INFOS[2]))
        {
            res = R.drawable.homepage_icon_bluetooth;
        }
        else if(tag.equals(HOME_BTNS_INFOS[3]))
        {
            res = R.drawable.homepage_icon_notice;
        }
        else if(tag.equals(HOME_BTNS_INFOS[4]))
        {
            res = R.drawable.homepage_icon_map;
        }
        return res;
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

        noticeDatas = Notice_DB.listAll(Notice_DB.class,"notice_Time desc");
        homeNoticeAdapter = new HomeNoticeAdapter(getHoldingActivity(),noticeDatas);
        noticeList.setAdapter(homeNoticeAdapter);
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
