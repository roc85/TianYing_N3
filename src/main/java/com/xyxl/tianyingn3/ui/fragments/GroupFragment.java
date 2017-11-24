package com.xyxl.tianyingn3.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.ui.customview.DragListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16 16:46
 * Version : V1.0
 * Introductions :
 */

public class GroupFragment extends Fragment implements FinalDatas {

    private static List<String> list = null;
    private DragListAdapter adapter = null;

    public static List<String> groupKey= new ArrayList<String>();
    private List<String> navList = new ArrayList<String>();
    private List<String> moreList = new ArrayList<String>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);



    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:

                    break;

                case 101:

                    break;
                default:
                    break;
            }
        };
    };

    private void initView(View view) {
        initData();

        DragListView dragListView = (DragListView)view.findViewById(R.id.drag_list);
        adapter = new DragListAdapter(getActivity(), list);
        dragListView.setAdapter(adapter);
    }

    private void initData() {
        //数据结果
//        list = new ArrayList<String>();
//
//        //groupKey存放的是分组标签
//        groupKey.add("A组");
//        groupKey.add("B组");
//
//        for(int i=0; i<5; i++){
//            navList.add("A选项"+i);
//        }
//        list.add("A组");
//        list.addAll(navList);
//
//        for(int i=0; i<8; i++){
//            moreList.add("B选项"+i);
//        }
//        list.add("B组");
//        list.addAll(moreList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        initView(view);
        return view;
    }


    public static class DragListAdapter extends ArrayAdapter<String> {

        public DragListAdapter(Context context, List<String> objects) {
            super(context, 0, objects);
        }

        public List<String> getList(){
            return list;
        }

        @Override
        public boolean isEnabled(int position) {
            if(groupKey.contains(getItem(position))){
                //如果是分组标签，返回false，不能选中，不能点击
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if(groupKey.contains(getItem(position))){
                //如果是分组标签，就加载分组标签的布局文件，两个布局文件显示效果不同
                view = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item_tag, null);
            }else{
                //如果是正常数据项标签，就加在正常数据项的布局文件
                view = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item, null);
            }

            TextView textView = (TextView)view.findViewById(R.id.drag_list_item_text);
            textView.setText(getItem(position));

            return view;
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
}
