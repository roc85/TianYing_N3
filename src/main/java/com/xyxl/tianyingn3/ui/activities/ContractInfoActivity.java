package com.xyxl.tianyingn3.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 联系人详情、编辑页面
 */
public class ContractInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView textName;
    private ImageView imageHead;
    private RelativeLayout headBox;
    private ImageView imageSendBd;
    private TextView textBdCardNum;
    private RelativeLayout bdCardBox;
    private TextView textPhone;
    private RelativeLayout PhoneBox;
    private TextView textremark;
    private RelativeLayout remarkBox;
    private Button buttonDel;
    private Button buttonShare;
    private Button buttonEdit;
    private ImageView imageSendPhone;

    private Contact_DB thisContact;
    private long _id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setExitFlag(0);
        setContentView(R.layout.activity_contract_info);

        initData();
        initView();

    }

    private void initData() {
        try {
            _id = getIntent().getExtras().getLong("contact_id");
            thisContact = Contact_DB.findById(Contact_DB.class,_id);
            if(thisContact==null)
            {
                finish();
            }
//            _id = Contact_DB.getIdViaName(thisContact.getContactName());
        } catch (Exception e) {
            finish();
            ;
        }
    }

    private void initView() {
        textName = (TextView) findViewById(R.id.textName);
        imageHead = (ImageView) findViewById(R.id.imageHead);
        headBox = (RelativeLayout) findViewById(R.id.headBox);
        imageSendBd = (ImageView) findViewById(R.id.imageSendBd);
        imageSendPhone = (ImageView) findViewById(R.id.imageSendPhone);
        textBdCardNum = (TextView) findViewById(R.id.textBdCardNum);
        bdCardBox = (RelativeLayout) findViewById(R.id.bdCardBox);
        textPhone = (TextView) findViewById(R.id.textPhone);
        PhoneBox = (RelativeLayout) findViewById(R.id.PhoneBox);
        textremark = (TextView) findViewById(R.id.textremark);
        remarkBox = (RelativeLayout) findViewById(R.id.remarkBox);
        buttonDel = (Button) findViewById(R.id.buttonDel);
        buttonShare = (Button) findViewById(R.id.buttonShare);
        buttonEdit = (Button) findViewById(R.id.buttonEdit);

        buttonDel.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        imageSendBd.setOnClickListener(this);
        imageSendPhone.setOnClickListener(this);

        //
        Picasso.with(this).load(new File(thisContact.getHead())).
                transform(transformation).error(R.mipmap.ic_launcher_round).
                placeholder(R.mipmap.ic_launcher_round).into(imageHead);

        textName.setText(thisContact.getContactName());
        if (!TextUtils.isEmpty(thisContact.getBdNum()) || BdCardBean.FormatCardNum("").equals(thisContact.getBdNum())) {
            textBdCardNum.setText(thisContact.getBdNum());
        } else {
            textBdCardNum.setText(getResources().getString(R.string.bd_card_num));
        }

        if (!TextUtils.isEmpty(thisContact.getPhone())) {
            textPhone.setText(thisContact.getPhone());
        } else {
            textPhone.setText(getResources().getString(R.string.phone_num));
        }

        if (!TextUtils.isEmpty(thisContact.getRemark())) {
            textremark.setText(thisContact.getRemark());
        } else {
            textremark.setText(getResources().getString(R.string.remark_info));
        }

        imageSendPhone = (ImageView) findViewById(R.id.imageSendPhone);
        imageSendPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDel:

                Contact_DB tmp = Contact_DB.findById(Contact_DB.class,_id);

                if(tmp!=null && tmp.delete())
                {
                    final String bdCard = tmp.getBdNum();
                    //更新所以消息列表
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<Message_DB> msgList = Message_DB.find(Message_DB.class,"send_Address = ? OR rcv_Address = ?",
                                    new String[]{bdCard, bdCard},
                                    null,"msg_Time desc",null);
                            if(msgList.size()>0)
                            {
                                for(int i=0;i<msgList.size();i++)
                                {
                                    Message_DB msg = msgList.get(i);
                                    if(msg.getRcvAddress().equals(bdCard))
                                    {
                                        msg.setRcvUserId(-1);
                                        msg.setRcvUserName(bdCard);
                                    }

                                    if(msg.getSendAddress().equals(bdCard))
                                    {
                                        msg.setSendUserId(-1);
                                        msg.setSendUserName(bdCard);
                                    }
                                    msg.save();
                                }

                            }
                        }
                    }).start();
                    finish();
                }


                break;
            case R.id.buttonShare:

                break;
            case R.id.buttonEdit:
                Intent intent = new Intent(this, NewContactActivity.class);
//                intent.putExtra("contact",thisContact);
                intent.putExtra("contact_id",_id);
                startActivity(intent);
                break;
            case R.id.imageSendPhone:

                break;
            case R.id.imageSendBd:

                break;
        }
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
    public void onResume() {
        super.onResume();
        thisContact = Contact_DB.findById(Contact_DB.class,_id);

        Picasso.with(this).load(new File(thisContact.getHead())).
                transform(transformation).placeholder(R.mipmap.ic_launcher_round).into(imageHead);

        textName.setText(thisContact.getContactName());
        if (!TextUtils.isEmpty(thisContact.getBdNum()) || BdCardBean.FormatCardNum("").equals(thisContact.getBdNum())) {
            textBdCardNum.setText(thisContact.getBdNum());
        } else {
            textBdCardNum.setText(getResources().getString(R.string.bd_card_num));
        }

        if (!TextUtils.isEmpty(thisContact.getPhone())) {
            textPhone.setText(thisContact.getPhone());
        } else {
            textPhone.setText(getResources().getString(R.string.phone_num));
        }

        if (!TextUtils.isEmpty(thisContact.getRemark())) {
            textremark.setText(thisContact.getRemark());
        } else {
            textremark.setText(getResources().getString(R.string.remark_info));
        }
    }
}
