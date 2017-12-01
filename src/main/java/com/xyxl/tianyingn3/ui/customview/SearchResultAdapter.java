package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.SearchResultsBean;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.FinalDatas;

import java.io.File;
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
    private LayoutInflater mInflater;
    private List<List<SearchResultsBean>> childList;
    private String keyword;

    static class ViewHolder
    {
        public ImageView imgHead;
        public TextView tvCon;
    }
    ViewHolder viewHolder = null;

    public SearchResultAdapter(Context c, List<String> groupList, List<List<SearchResultsBean>> childList, String key)
    {
        mContext = c;
        this.groupList = groupList;
        this.childList = childList;
        this.keyword = key;
        mInflater = LayoutInflater.from(mContext);
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

        TextView textView = getTextView();
        textView.setTextColor(0xFF636363);
        textView.setBackgroundColor(0xFFFFFFFF);
        textView.setText(groupList.get(groupPosition));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(14,8,0,8);
        textView.setLayoutParams(params);
        return textView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(
                R.layout.search_result_item, null);

        viewHolder = new ViewHolder();

        viewHolder.imgHead = (ImageView) convertView
                .findViewById(R.id.imageIcon);
        viewHolder.tvCon = (TextView) convertView
                .findViewById(R.id.textCon);

        if(childList.get(groupPosition).get(childPosition).getType() == 0)
        {
            Contact_DB tmp = Contact_DB.findById(Contact_DB.class,childList.get(groupPosition).get(childPosition).getDbId());
            if(tmp != null)
            {
                Picasso.with(mContext).load(new File(tmp.getHead())).
                        transform(transformation).
                        placeholder(R.mipmap.ic_launcher_round).
                        into(viewHolder.imgHead);
            }
        }
        else if(childList.get(groupPosition).get(childPosition).getType() == 1)
        {
            Message_DB tmp = Message_DB.findById(Message_DB.class,childList.get(groupPosition).get(childPosition).getDbId());
            if(tmp != null)
            {
                Contact_DB tmpCon = Contact_DB.findById(Contact_DB.class,tmp.getRcvUserId());
                if(tmpCon != null)
                {
                    Picasso.with(mContext).load(new File(tmpCon.getHead())).
                            transform(transformation).
                            placeholder(R.mipmap.ic_launcher_round).
                            into(viewHolder.imgHead);
                }

            }
        }

        String con = childList.get(groupPosition).get(childPosition).toString();
        int index = con.toLowerCase().indexOf(keyword.toLowerCase());

        if (index >= 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(con);

            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK);
            ForegroundColorSpan keySpan = new ForegroundColorSpan(0xFF68E580);

            builder.setSpan(blackSpan, 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(keySpan, index, keyword.length() + index, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setSpan(blackSpan, keyword.length() + index, con.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            viewHolder.tvCon.setText(builder);
        } else {
            viewHolder.tvCon.setText(con);
        }

        return convertView;
    }

    Transformation transformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            int width = source.getWidth();
            int height = source.getHeight();
            int size = Math.min(width, height);
            Bitmap blankBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(blankBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(source, 0, 0, paint);
            if (source != null && !source.isRecycled()) {
                source.recycle();
            }
            return blankBitmap;
        }

        @Override
        public String key() {
            return "squareup";
        }
    };

    @Override
    public boolean isChildSelectable(int groupPosition,
                                     int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }
}
