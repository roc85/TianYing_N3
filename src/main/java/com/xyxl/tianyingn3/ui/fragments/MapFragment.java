package com.xyxl.tianyingn3.ui.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.database.PosInfo_DB;

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


    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        mMapView = (MapView) view.findViewById(R.id.mapFragmentView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //自定义图标显示自身位置
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.search_bar_icon_normal);
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
        option.setCoorType("wgs84ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
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
            MyPosition.getInstance().setMyLon(mCurrentLon);
            MyPosition.getInstance().setMyLat(mCurrentLat);
            myPos = new LatLng(mCurrentLat, mCurrentLon);

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

    private void RefreshSavedPos()
    {
        mBaiduMap.clear();
        recordList = PosInfo_DB.listAll(PosInfo_DB.class);
        recordPointList.clear();
        recordMarkerList.clear();
        for(int i=0;i<recordList.size();i++)
        {
            recordPointList.add(new LatLng(recordList.get(i).getLat(), recordList.get(i).getLon()));
        }

        //画记录点
        for(int i=0;i<recordPointList.size();i++)
        {
            bdA = BitmapDescriptorFactory.fromResource(R.drawable.search_bar_icon_normal);
            MarkerOptions ooA = new MarkerOptions().position(recordPointList.get(i)).anchor(0.5f, 0.5f).icon(bdA).zIndex(5).draggable(true);
            ooA.animateType(MarkerOptions.MarkerAnimateType.grow);

            recordMarkerList.add((Marker) mBaiduMap.addOverlay(ooA));

        }
    }

    /**
     * 清除所有Overlay
     */
    public void clearOverlay() {
        myMarker=null;

        mBaiduMap.clear();

        bdB = BitmapDescriptorFactory.fromResource(R.drawable.search_bar_icon_normal);
        MarkerOptions ooA = new MarkerOptions().position(myPos).anchor(0.5f, 0.5f).icon(bdB).zIndex(5).draggable(true);
        myMarker = (Marker)mBaiduMap.addOverlay(ooA);
        try {
            bdA.recycle();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void ShowMyPos()
    {
        bdB = BitmapDescriptorFactory
                .fromResource(R.drawable.search_bar_icon_normal);
        MarkerOptions ooA = new MarkerOptions().position(myPos)
                .anchor(0.5f, 0.5f).icon(bdB).zIndex(5)
                .draggable(true);
        if (myMarker != null) {
            myMarker.setPosition(myPos);
        } else {
            myMarker = (Marker) mBaiduMap.addOverlay(ooA);
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
