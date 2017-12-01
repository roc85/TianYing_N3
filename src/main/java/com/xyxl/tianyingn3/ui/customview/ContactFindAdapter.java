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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.SearchResultsBean;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.util.TimeUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/29 14:56
 * Version : V1.0
 * Introductions :
 */

public class ContactFindAdapter extends BaseAdapter implements FinalDatas {

    private Context mContext;
    private List<Contact_DB> contactList;
    private LayoutInflater mInflater;
    private String keyword;

    static class ViewHolder
    {
        public TextView tvName, tvNum;
    }
    ViewHolder viewHolder = null;

    public ContactFindAdapter(Context c, List<Contact_DB> contactList, String key)
    {
        mContext = c;
        this.contactList = contactList;
        this.keyword = key;
        mInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Contact_DB getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Contact_DB entity = contactList.get(position);

        if (convertView == null)
        {
            convertView = mInflater.inflate(
                    R.layout.contact_find_item, null);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.textfindname);
            viewHolder.tvNum = (TextView) convertView
                    .findViewById(R.id.textfindnum);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvName.setText(entity.getContactName());

        String con = entity.getBdNum()+" "+entity.getPhone();
        int index = con.indexOf(keyword);

        if (index >= 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(con);

            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            ForegroundColorSpan blackSpan = new ForegroundColorSpan(0xFF636363);
            ForegroundColorSpan keySpan = new ForegroundColorSpan(0xFF5BC970);

            builder.setSpan(blackSpan, 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(keySpan, index, keyword.length() + index, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setSpan(blackSpan, keyword.length() + index, con.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            viewHolder.tvNum.setText(builder);
        }
        else
        {
            viewHolder.tvNum.setText(con);
        }


        return convertView;
    }

}
