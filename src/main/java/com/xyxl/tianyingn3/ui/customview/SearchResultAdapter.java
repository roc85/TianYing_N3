package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.SearchResultsBean;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.global.FinalDatas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/22 14:32
 * Version : V1.0
 * Introductions :
 */

public class SearchResultAdapter extends BaseExpandableListAdapter implements FinalDatas {
    
    private Context mContext;
    private List<String> groupList;

    private List<List<SearchResultsBean>> childList;

    public SearchResultAdapter(Context c, List<String> groupList, List<List<SearchResultsBean>> childList)
    {
        mContext = c;
        this.groupList = groupList;
        this.childList = childList;
    }
//    //设置组视图的图片
    int[] logos = new int[] { R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal,R.drawable.search_bar_icon_normal};
//    //设置组视图的显示文字
//    private String[] generalsTypes = new String[] { "魏", "蜀", "吴" };
//    //子视图显示文字
//    private String[][] generals = new String[][] {
//            { "夏侯惇", "甄姬", "许褚", "郭嘉", "司马懿", "杨修" },
//            { "马超", "张飞", "刘备", "诸葛亮", "黄月英", "赵云" },
//            { "吕蒙", "陆逊", "孙权", "周瑜", "孙尚香" }
//
//    };
//    //子视图图片
//    public int[][] generallogos = new int[][] {
//            { R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal,
//                    R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal,
//                    R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal },
//            { R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal,
//                    R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal,
//                    R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal },
//            { R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal,
//                    R.drawable.search_bar_icon_normal, R.drawable.search_bar_icon_normal } };
//
    //自己定义一个获得文字信息的方法
    TextView getTextView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, 64);
        TextView textView = new TextView(
                mContext);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(10, 0, 0, 0);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        return textView;
    }


    //重写ExpandableListAdapter中的各个方法
    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groupList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LinearLayout ll = new LinearLayout(
                mContext);
        ll.setOrientation(LinearLayout.HORIZONTAL);
//        ImageView logo = new ImageView(mContext);
//        logo.setImageResource(logos[groupPosition]);
//        logo.setPadding(10, 0, 0, 0);
//        ll.addView(logo);
        TextView textView = getTextView();
        textView.setTextColor(Color.BLACK);
        textView.setText(groupList.get(groupPosition));
        ll.addView(textView);

        return ll;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LinearLayout ll = new LinearLayout(
                mContext);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = getTextView();
        textView.setPadding(40, 0, 0, 0);
        textView.setText(childList.get(groupPosition).get(childPosition).toString());
        ll.addView(textView);
        return ll;
    }

    @Override
    public boolean isChildSelectable(int groupPosition,
                                     int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }
}
