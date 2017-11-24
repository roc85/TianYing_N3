package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.util.TimeUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17 10:47
 * Version : V1.0
 * Introductions : 整体消息列表适配器
 */

public class TotalMsgAdapter extends BaseAdapter {

    private List<Message_DB> msgList;
    private LayoutInflater mInflater;
    private Context mContext;
    static class ViewHolder
    {
        public ImageView imgHead;
        public TextView tvName, tvTime, tvCon;
    }
    ViewHolder viewHolder = null;

    public TotalMsgAdapter(Context context, List<Message_DB> datalist) {
        mContext = context;
        this.msgList = datalist;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
//        return msgList.get(position).getMsgId();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message_DB entity = msgList.get(position);

        if (convertView == null)
        {
            convertView = mInflater.inflate(
                    R.layout.total_msg_item, null);

            viewHolder = new ViewHolder();
            viewHolder.imgHead = (ImageView) convertView
                    .findViewById(R.id.tmHead);
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.tmName);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.tmTime);
            viewHolder.tvCon = (TextView) convertView
                    .findViewById(R.id.tmCon);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if(entity.getMsgType() == 0)
//        {
//            viewHolder.tvName.setText(entity.getRcvUserName());
//
//        }
//        else if(entity.getMsgType() == 1)
//        {
//            viewHolder.tvName.setText(entity.getSendUserName());
//
//        }

        viewHolder.tvTime.setText(TimeUtil.formatFriendly(new Date(entity.getMsgTime())));

        viewHolder.tvCon.setText(entity.getMsgCon().length() > 10?(entity.getMsgCon().substring(0,10)+"..."):entity.getMsgCon());

        if(entity.getMsgType() == 0)
        {
            if(entity.getRcvUserId() >= 0)
            {
                Contact_DB tmp = Contact_DB.findById(Contact_DB.class,entity.getRcvUserId());
                if(tmp!=null)
                {
                    viewHolder.tvName.setText(tmp.getContactName());
                    Picasso.with(mContext).load(new File(tmp.getHead())).
                            transform(transformation).
                            placeholder(R.mipmap.ic_launcher_round).
                            into(viewHolder.imgHead);
                }
                else
                {
                    viewHolder.tvName.setText(entity.getRcvUserName());
                }


            }
            else
            {
                viewHolder.tvName.setText(entity.getRcvUserName());
            }

        }
        else if(entity.getMsgType() == 1)
        {
            if(entity.getSendUserId() >= 0)
            {
                Contact_DB tmp = Contact_DB.findById(Contact_DB.class,entity.getSendUserId());
                viewHolder.tvName.setText(tmp.getContactName());
                Picasso.with(mContext).load(new File(tmp.getHead())).
                        transform(transformation).
                        placeholder(R.mipmap.ic_launcher_round).
                        into(viewHolder.imgHead);

            }
            else
            {
                viewHolder.tvName.setText(entity.getSendUserName());
            }
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
}
