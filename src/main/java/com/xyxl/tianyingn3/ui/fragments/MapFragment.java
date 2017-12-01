package com.xyxl.tianyingn3.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.database.PosInfo_DB;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.solutions.Gps;
import com.xyxl.tianyingn3.solutions.GpsData;
import com.xyxl.tianyingn3.ui.activities.ChatActivity;
import com.xyxl.tianyingn3.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:57
 * Version : V1.0
 * Introductions : 位置功能页面
 */

public class MapFragment extends BaseFragment{

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private BitmapDescriptor mCurrentMarker;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    MapView mMapView;
    BaiduMap mBaiduMap;

    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;

    //地理编码相关
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private String myCity = "";
    //POI搜索相关
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;

    //友邻位置显示相关
    private List<LatLng> recordPointList = new ArrayList<LatLng>();
    private List<PosInfo_DB> recordList = new ArrayList<PosInfo_DB>();
    private List<Marker> recordMarkerList = new ArrayList<Marker>();

    private List<LatLng> ruleList = new ArrayList<LatLng>();
    private List<LatLng> recordToMyList = new ArrayList<LatLng>();
    private List<Marker> favoMarkerList = new ArrayList<Marker>();

    BitmapDescriptor bdA ,bdB;
    Marker myMarker , mapChooseMarker;
    Polyline mPolyline;
    LatLng myPos;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    //自定义UI
    private RelativeLayout myIcon, otherIcon;
    private ImageView myImgBack , otherBack;
    private Contact_DB myContact;

    private RelativeLayout infoBox;
    private TextView tvName, tvCon;

    private InfoWindow mInfoWindow;

    //工作线程
    private LocThread locThread;
    private boolean needFollow = false;
    //
    private ImageView ivBigger, ivSmaller, ivMyPos;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        infoBox = (RelativeLayout)view.findViewById(R.id.mapInfoBox);
        tvName =(TextView)view.findViewById(R.id.textMapInfoName);
        tvCon =(TextView)view.findViewById(R.id.textMapInfoCon);
        infoBox.setVisibility(View.GONE);

        ivMyPos = (ImageView) view.findViewById(R.id.img_position);
        ivMyPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needFollow = !needFollow;

                RefreshSavedPos();

                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(myPos);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });

        initUI();

        mMapView = (MapView) view.findViewById(R.id.mapFragmentView);
        mBaiduMap = mMapView.getMap();

        // 隐藏指南针
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(false);
        // 删除百度地图logo
        mMapView.removeViewAt(1);
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(false);

        //自定义图标显示自身位置

//        mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.search_bar_icon_normal);
//        mCurrentMarker = BitmapDescriptorFactory
//                .fromView(myIcon);
        mBaiduMap
                .setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker,
                        accuracyCircleFillColor, accuracyCircleStrokeColor));

        //设置地图模式
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        mBaiduMap
                .setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        initMapListener();
        //
//        RefreshSavedPos();

        //定位线程
        locThread = new LocThread();
        locThread.isPause = false;
        locThread.isRun = true;
        locThread.start();
    }

    private void initMapListener() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(marker == myMarker)
                {
                    byte[] lon = GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon());
                    byte[] lat = GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat());
                    showInfoBox("我的位置", GpsData.GetPosDisplayStr(lon)+","+GpsData.GetPosDisplayStr(lat));
                    mHandler.sendEmptyMessageDelayed(1001, 3000);


                }
                for(int i=0; i<recordMarkerList.size();i++)
                {
                    if(marker == recordMarkerList.get(i))
                    {
                        showInfoBox(recordList.get(i).getUserName(), recordList.get(i).getLon()+","+recordList.get(i).getLat());
                        showAddBtnBox(marker.getPosition(),recordList.get(i).getUserNum());
                        mHandler.sendEmptyMessageDelayed(1001, 3000);
                    }
                }
                return false;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    private void showInfoBox(String name, String con)
    {
        tvName.setText(name);
        tvCon.setText(con);
        infoBox.setVisibility(View.VISIBLE);
        infoBox.startAnimation(AnimationUtils.loadAnimation(getHoldingActivity(),R.anim.box_alpha_show));

    }

    private void hideInfoBox()
    {
        infoBox.setVisibility(View.GONE);
        infoBox.startAnimation(AnimationUtils.loadAnimation(getHoldingActivity(),R.anim.box_alpha_hide));
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:

                    break;

                case 101:

                    break;

                case 1001:
                    //隐藏信息栏
                    hideInfoBox();
                    break;
                case 1002:
                    //隐藏信息栏
                    if(mInfoWindow!=null)
                    {
                        mBaiduMap.hideInfoWindow();
                    }

                    break;
                default:
                    break;
            }
        };
    };
    private void initUI() {
        try
        {
            myContact = Contact_DB.findById(Contact_DB.class, Contact_DB.getIdViaAddress(BdCardBean.getInstance().getIdNum()));
        }
        catch(Exception e)
        {

        }


        RelativeLayout.LayoutParams relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(45f), getHoldingActivity().getMyDpHeight(45f));

        myIcon = new RelativeLayout(getHoldingActivity());
//        myIcon.setBackgroundResource(R.drawable.location_touxiang_bac_me);
//        myIcon.setBackgroundColor(0xFFFFFFFF);
        myIcon.setLayoutParams(relaParams);

        myImgBack = new ImageView(getHoldingActivity());
        relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(45f), getHoldingActivity().getMyDpHeight(45f));
        relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        myImgBack.setImageResource(R.drawable.location_touxiang_bac_me);
        myImgBack.setLayoutParams(relaParams);
        myIcon.addView(myImgBack);

        ImageView myImg = new ImageView(getHoldingActivity());
        relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(40f), getHoldingActivity().getMyDpHeight(40f));
        relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        myImg.setLayoutParams(relaParams);
        if(myContact == null)
        {
            myImg.setBackgroundResource(R.mipmap.ic_launcher_round);
        }
        else
        {
            try
            {
                Bitmap backImg = BitmapFactory.decodeFile(myContact.getHead());
                backImg = ImageUtil.getRoundedCornerBitmap(backImg,backImg.getWidth()/2);
                myImg.setImageBitmap(backImg);
            }
            catch(Exception e)
            {
                myImg.setBackgroundResource(R.mipmap.ic_launcher_round);
            }

        }
        myIcon.addView(myImg);

    }

    private View GetOtherIcon(long _id)
    {
        Contact_DB otherContact = null;
        try
        {
            otherContact = Contact_DB.findById(Contact_DB.class, _id);
        }
        catch(Exception e)
        {

        }


        RelativeLayout.LayoutParams relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(45f), getHoldingActivity().getMyDpHeight(45f));

        otherIcon = new RelativeLayout(getHoldingActivity());
//        myIcon.setBackgroundResource(R.drawable.location_touxiang_bac_me);
//        myIcon.setBackgroundColor(0xFFFFFFFF);
        otherIcon.setLayoutParams(relaParams);

        otherBack = new ImageView(getHoldingActivity());
        relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(45f), getHoldingActivity().getMyDpHeight(45f));
        relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        otherBack.setImageResource(R.drawable.location_touxiang_bac_others);
        otherBack.setLayoutParams(relaParams);
        otherIcon.addView(otherBack);

        ImageView myImg = new ImageView(getHoldingActivity());
        relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(40f), getHoldingActivity().getMyDpHeight(40f));
        relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        myImg.setLayoutParams(relaParams);
        if(otherContact == null)
        {
            myImg.setBackgroundResource(R.mipmap.ic_launcher_round);
        }
        else
        {
            try
            {
                Bitmap backImg = BitmapFactory.decodeFile(otherContact.getHead());
                backImg = ImageUtil.getRoundedCornerBitmap(backImg,backImg.getWidth()/2);
                myImg.setImageBitmap(backImg);
            }
            catch(Exception e)
            {
                myImg.setBackgroundResource(R.mipmap.ic_launcher_round);
            }

        }
        otherIcon.addView(myImg);

        return otherIcon;
    }



    private class LocThread extends Thread {
        //用于控制线程开启关闭
        protected boolean isRun, isPause;

        @Override
        public void run() {
            super.run();

            //完成线程内功能
            while (isRun) {

                while (!isPause) {
                    Log.i("地图暂停", "OK");
                    try
                    {
                        if(needFollow && myPos!=null)
                        {
                            MapStatus.Builder builder = new MapStatus.Builder();
                            builder.target(myPos);
                            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                            sleep(1000);
                        }
                    }
                    catch(Exception e)
                    {

                    }

                }
            }
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();

            if(SettingSharedPreference.getDataInt(getHoldingActivity(),LOCATION_TYPE_FLAG) == 1)
            {
                LatLng changePos = ChangePos2Wgs(mCurrentLon, mCurrentLat);
                MyPosition.getInstance().setMyLon(changePos.longitude);
                MyPosition.getInstance().setMyLat(changePos.latitude);
                myPos = new LatLng(mCurrentLat, mCurrentLon);
                ShowMyPos();
            }


            mCurrentAccracy = location.getRadius();


            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())

                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);


            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    private LatLng ChangePos2Wgs(double lon, double lat)
    {
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        LatLng sourceLatLng = new LatLng(lat, lon);
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    private LatLng ChangePos2Bd09(double lon, double lat)
    {
        Gps p = GpsData.bd09_To_Gps84(lat, lon);
        LatLng desLatLng = new LatLng(p.getWgLat(), p.getWgLon());
        return desLatLng;
    }

    private void RefreshSavedPos()
    {
        clearOverlay();
        recordList = PosInfo_DB.listAll(PosInfo_DB.class);
        recordPointList.clear();
        recordMarkerList.clear();
        for(int i=0;i<recordList.size();i++)
        {
            recordPointList.add(ChangePos2Wgs(recordList.get(i).getLat(), recordList.get(i).getLon()));
        }

        //画记录点
        for(int i=0;i<recordPointList.size();i++)
        {
//            bdA = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
            bdA = BitmapDescriptorFactory.fromView(GetOtherIcon(recordList.get(i).getUserId()));
            MarkerOptions ooA = new MarkerOptions().position(recordPointList.get(i)).anchor(0.5f, 0.5f).icon(bdA).zIndex(5).draggable(true);
//            MarkerOptions ooA = new MarkerOptions().
//                    position(p).anchor(0.5f, 0.5f).icon(bdA).zIndex(5).draggable(true);
            ooA.animateType(MarkerOptions.MarkerAnimateType.grow);

            recordMarkerList.add((Marker) mBaiduMap.addOverlay(ooA));
//            mBaiduMap.addOverlay(ooA);

        }

        ShowMyPos();
    }


    private void ShowMyPos()
    {
//        bdB = BitmapDescriptorFactory
//                .fromResource(R.drawable.search_bar_icon_normal);
        bdB = BitmapDescriptorFactory.fromView(myIcon);
        MarkerOptions ooA = new MarkerOptions().position(myPos)
                .anchor(0.5f, 0.5f).icon(bdB).zIndex(5)
                .draggable(true);
        if (myMarker != null) {

            myMarker.setPosition(myPos);
        } else {
            myMarker = (Marker) mBaiduMap.addOverlay(ooA);
        }

//        MapStatus.Builder builder = new MapStatus.Builder();
//        builder.target(myPos);
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    private void showAddBtnBox(LatLng pos, final String bdNum)
    {
        mBaiduMap.hideInfoWindow();
        //显示提示框
        RelativeLayout.LayoutParams relaParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RelativeLayout btnsBox = new RelativeLayout(getHoldingActivity());
        btnsBox.setLayoutParams(relaParams);
//        btnsBox.setBackgroundColor(0xFFFFFFFF);

        ImageView ivLeft = new ImageView(getHoldingActivity());
        ivLeft.setBackgroundResource(R.drawable.location_icon_send_massage);
        RelativeLayout.LayoutParams relaParamsLeft =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(30f), getHoldingActivity().getMyDpHeight(30));
//        relaParamsLeft.setMargins(0,0,0,getHoldingActivity().getMyDpWidth(90f));
//        relaParamsLeft.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParamsLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        ivLeft.setLayoutParams(relaParamsLeft);

        ImageView ivRight = new ImageView(getHoldingActivity());
        ivRight.setBackgroundResource(R.drawable.location_icon_send_location);
        RelativeLayout.LayoutParams relaParamsRight =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(30f), getHoldingActivity().getMyDpHeight(30));
        relaParamsRight.setMargins(getHoldingActivity().getMyDpWidth(90f),0,0,0);
//        relaParamsRight.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParamsRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ivRight.setLayoutParams(relaParamsRight);

        btnsBox.addView(ivLeft);
        btnsBox.addView(ivRight);

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact_DB tmp = Contact_DB.findById(Contact_DB.class,Contact_DB.getIdViaAddress(bdNum));
                Message_DB msg = new Message_DB();
                if(tmp != null)
                {
                    msg.setMsgType(1);
                    msg.setSendAddress(tmp.getBdNum());
                    msg.setSendUserName(tmp.getContactName());
                    msg.setSendUserId(tmp.getId());
                    Intent intent = new Intent(getActivity(),ChatActivity.class);
                    intent.putExtra("msg",msg);
                    startActivity(intent);
                }
                else
                {
                    msg.setMsgType(1);
                    msg.setSendAddress(bdNum);
                    msg.setSendUserName(bdNum);
                    msg.setSendUserId(-1);
                    Intent intent = new Intent(getActivity(),ChatActivity.class);
                    intent.putExtra("msg",msg);
                    startActivity(intent);
                }
            }
        });
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().ShowToast("right");
            }
        });
        mInfoWindow = new InfoWindow(btnsBox, pos, getHoldingActivity().getMyDpHeight(15f));

//        ivLeft.startAnimation(AnimationUtils.loadAnimation(getHoldingActivity(),R.anim.box_alpha_show));
//        ivRight.startAnimation(AnimationUtils.loadAnimation(getHoldingActivity(),R.anim.box_alpha_show));
        mBaiduMap.showInfoWindow(mInfoWindow);

//        mHandler.sendEmptyMessageDelayed(1002,5000);

    }
    /**
     * 清除所有Overlay
     */
    public void clearOverlay() {
        myMarker=null;

        mBaiduMap.clear();

        ShowMyPos();
        try {
            bdA.recycle();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
//                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        //取消注册传感器监听
//        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
//        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
