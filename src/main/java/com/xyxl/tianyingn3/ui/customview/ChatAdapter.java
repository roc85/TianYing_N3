package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.TextUtils;
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
import com.xyxl.tianyingn3.solutions.GpsData;
import com.xyxl.tianyingn3.util.DataUtil;
import com.xyxl.tianyingn3.util.TimeUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/21 15:25
 * Version : V1.0
 * Introductions :
 */

public class ChatAdapter extends BaseAdapter {
    private List<Message_DB> msgList;
    private LayoutInflater mInflater;
    private Context mContext;

    private long priTime = 0;
    static class ViewHolder
    {
        public ImageView imgHead;
        public TextView tvPos, tvTime, tvCon, tvSend;
    }
    ViewHolder viewHolder = null;

    public ChatAdapter(Context context, List<Message_DB> datalist) {
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
        return 0;
    }

    @Override
    public synchronized View getView(int position, View convertView, ViewGroup parent) {
        Message_DB entity = msgList.get(position);

//        if (convertView == null)
//        {
            viewHolder = new ViewHolder();
            if(entity.getMsgType() == 0)
            {
                convertView = mInflater.inflate(
                        R.layout.chat_send_item, null);
                viewHolder.tvSend = (TextView) convertView
                        .findViewById(R.id.textSendStatus);
            }
            else if(entity.getMsgType() == 1)
            {
                convertView = mInflater.inflate(
                        R.layout.chat_rcv_item, null);

            }

            viewHolder.imgHead = (ImageView) convertView
                    .findViewById(R.id.imageChatRcvHead);
            viewHolder.tvPos = (TextView) convertView
                    .findViewById(R.id.textRcvPos);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.textTimeRcv);
            viewHolder.tvCon = (TextView) convertView
                    .findViewById(R.id.textRcv);
            convertView.setTag(viewHolder);

//        }
//        else
//        {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }


        //发送状态
        try {
            if (entity.getMsgType() == 0) {
                if (entity.getMsgSendStatue() == 0) {
                    viewHolder.tvSend.setText(mContext.getResources().getString(R.string.sending));
                } else if (entity.getMsgSendStatue() == 2) {
                    viewHolder.tvSend.setText(mContext.getResources().getString(R.string.send_failed));
                } else {
                    viewHolder.tvSend.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {

        }


        //消息时间
        if(position == 0 || entity.getMsgTime()-msgList.get(position-1).getMsgTime()>120*1000)
        {
            priTime = entity.getMsgTime();
            viewHolder.tvTime.setText(TimeUtil.formatDateTime(new Date(entity.getMsgTime())));
        }
        else
        {
            viewHolder.tvTime.setVisibility(View.INVISIBLE);
        }

        //位置信息
        if (TextUtils.isEmpty(entity.getMsgPos())) {
            viewHolder.tvPos.setVisibility(View.GONE);
        } else {
            try
            {
                String pos = "POS:";
                byte[] lon = DataUtil.hexStr2Bytes(entity.getMsgPos().substring(0,8));
                byte[] lat = DataUtil.hexStr2Bytes(entity.getMsgPos().substring(8));
                pos += (GpsData.GetPosDisplayStr(lon)+" "+GpsData.GetPosDisplayStr(lat));
                viewHolder.tvPos.setText(pos);
            }
            catch(Exception e)
            {
                viewHolder.tvPos.setVisibility(View.GONE);
            }

        }

        //具体内容
        viewHolder.tvCon.setText(entity.getMsgCon());

        //头像
//        if (entity.getMsgType() == 0) {
//            Contact_DB tmp = Contact_DB.findById(Contact_DB.class,entity.getSendUserId());
//            Picasso.with(mContext).load(new File(tmp.getHead())).
//                    transform(transformation).
//                    placeholder(R.mipmap.ic_launcher_round).
//                    into(viewHolder.imgHead);
//        }
//        else if (entity.getMsgType() == 1) {
            Contact_DB tmp = Contact_DB.findById(Contact_DB.class,entity.getSendUserId());
            if(tmp!=null)
            {
                Picasso.with(mContext).load(new File(tmp.getHead())).
                        transform(transformation).
                        placeholder(R.mipmap.ic_launcher_round).
                        into(viewHolder.imgHead);
            }

//        }


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
