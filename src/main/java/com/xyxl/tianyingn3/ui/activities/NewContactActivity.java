package com.xyxl.tianyingn3.ui.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.App;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.util.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 新建联系人页面
 */
public class NewContactActivity extends BaseActivity implements View.OnClickListener {

    private EditText edName;
    private EditText edNum;
    private Button btnAdd;

    private FloatingActionButton addNew;
    private ImageView imageHead, ivBack;
    private LinearLayout headBox;
    private EditText editName;
    private EditText editBdNum;
    private EditText editPhone;
    private EditText editRemark;

    private TextView tvSave, tvEditHead;

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    // 创建一个以当前时间为名称的文件
    File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());

    private String photoFile, bdCard, name;
    private long saveId;
    private Contact_DB thisContact;
    private boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setContentView(R.layout.activity_new_contract);

        initView();
    }

    private void initView() {
//        edName = (EditText) findViewById(R.id.edName);
//        edNum = (EditText) findViewById(R.id.edNum);
//        btnAdd = (Button) findViewById(R.id.btnAdd);
//
//        btnAdd.setOnClickListener(this);


        addNew = (FloatingActionButton) findViewById(R.id.addNew);
        addNew.setOnClickListener(this);
        imageHead = (ImageView) findViewById(R.id.imageHead);
        imageHead.setOnClickListener(this);
        headBox = (LinearLayout) findViewById(R.id.headEditBox);
        headBox.setOnClickListener(this);

        editName = (EditText) findViewById(R.id.editName);
        editName.setOnClickListener(this);
        editBdNum = (EditText) findViewById(R.id.editBdNum);
        editBdNum.setOnClickListener(this);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editPhone.setOnClickListener(this);
        editRemark = (EditText) findViewById(R.id.editRemark);
        editRemark.setOnClickListener(this);
        tvSave = (TextView) findViewById(R.id.textSave);
        tvSave.setOnClickListener(this);
        tvEditHead = (TextView) findViewById(R.id.textEditHead);
        tvEditHead.setOnClickListener(this);
        ivBack = (ImageView) findViewById(R.id.imageBack);
        ivBack.setOnClickListener(this);

        //新加陌生号码
        try
        {
            editBdNum.setText(getIntent().getExtras().getString("num"));
        }
        catch(Exception e)
        {

        }
        //编辑联系人
        try
        {
            saveId = getIntent().getExtras().getLong("contact_id");
            thisContact = Contact_DB.findById(Contact_DB.class,saveId);
//            saveId = getIntent().getIntExtra("c_id",-1);
            if(thisContact != null)
            {
                editBdNum.setText(thisContact.getBdNum());
                editName.setText(thisContact.getContactName());
                editPhone.setText(thisContact.getPhone());
                editRemark.setText(thisContact.getRemark());
                photoFile = thisContact.getHead();
                Picasso.with(this).load(new File(thisContact.getHead())).
                        transform(transformation).
                        placeholder(R.mipmap.ic_launcher_round).
                        into(imageHead);
                isNew = false;
            }

        }
        catch(Exception e)
        {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.textSave:
                submit();
                break;
            case R.id.headEditBox:
                showDialog();
                break;
            case R.id.imageHead:
                showDialog();
                break;
            case R.id.textEditHead:
                showDialog();
                break;
            case R.id.imageBack:
                finish();
                break;
        }
    }


    private void submit() {
        // validate
        String editNameString = editName.getText().toString().trim();
        if (TextUtils.isEmpty(editNameString)) {
            Toast.makeText(this, getResources().getString(R.string.name_cant_null), Toast.LENGTH_SHORT).show();
            return;
        }


        if(isNew && Contact_DB.HaveSameName(editNameString))
        {
            Toast.makeText(this, getResources().getString(R.string.have_same_name), Toast.LENGTH_SHORT).show();
            return;
        }

        String editBdNumString = editBdNum.getText().toString().trim();
        if(isNew &&Contact_DB.HaveSameNum(editBdNumString))
        {
            Toast.makeText(this, getResources().getString(R.string.have_same_num), Toast.LENGTH_SHORT).show();
            return;
        }
//        if (TextUtils.isEmpty(editBdNumString)) {
//            Toast.makeText(this, "北斗卡号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        String editPhoneString = editPhone.getText().toString().trim();
        if(isNew &&Contact_DB.HaveSamePhone(editPhoneString))
        {
            Toast.makeText(this, getResources().getString(R.string.have_same_phone), Toast.LENGTH_SHORT).show();
            return;
        }
//        if (TextUtils.isEmpty(editPhoneString)) {
//            Toast.makeText(this, "手机号码", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String editRemarkString = editRemark.getText().toString().trim();
//        if (TextUtils.isEmpty(editRemarkString)) {
//            Toast.makeText(this, "备注信息", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // TODO validate success, do something
        if(thisContact == null)
        {
            thisContact = new Contact_DB();
        }
        else
        {
//            long _id = Contact_DB.getIdViaName(thisContact.getContactName());
            thisContact = Contact_DB.findById(Contact_DB.class,saveId);
        }
//        Contact_DB t = new Contact_DB();
        thisContact.setBdNum(BdCardBean.FormatCardNum(editBdNum.getText().toString()));
        thisContact.setContactName(editName.getText().toString());
        thisContact.setContactOwner("");
        thisContact.setContactType(0);
        if(TextUtils.isEmpty(photoFile))
        {
            thisContact.setHead("");
        }
        else
        {
            thisContact.setHead(photoFile);
        }

        thisContact.setPassword("");
        thisContact.setPhone(editPhone.getText().toString());
        thisContact.setRemark(editRemark.getText().toString());
        saveId = thisContact.save();

        AppBus.getInstance().post(thisContact);
        bdCard = thisContact.getBdNum();
        name =thisContact.getContactName();
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
                            msg.setRcvUserId(saveId);
                            msg.setRcvUserName(name);
                        }

                        if(msg.getSendAddress().equals(bdCard))
                        {
                            msg.setSendUserId(saveId);
                            msg.setSendUserName(name);
                        }
                        msg.save();
                    }

                }
            }
        }).start();

        finish();

    }

    //提示对话框方法
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("头像设置")
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        // 调用系统的拍照功能
                        //获取权限
                        if (isMarshmallow()) {
                            requestPosPermission();//然后在回调中处理
                        }
                        else
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // 指定调用相机拍照后照片的储存路径
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                            startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                        }

                    }
                })
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();
    }

    private boolean isMarshmallow()
    {
        return Build.VERSION.SDK_INT >= 23;
    }

    private boolean isNougat()
    {
        return Build.VERSION.SDK_INT >= 24;
    }

    private void requestPosPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    3);
        }
        else
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 指定调用相机拍照后照片的储存路径

//            Uri photoURI = FileProvider.getUriForFile(NewContactActivity.this, getApplicationContext().getPackageName() + ".provider", tempFile);
            if (isNougat())
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageContentUri(tempFile));
            }
            else
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            }

            startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
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
            if(granted)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
            }
        }
    }

    /**
     * 把 Bitmap 保存在SD卡路径后，返回file 类型的 uri
     *
     * @param bm
     * @return
     */
    private Uri saveBitmap(Bitmap bm) {


        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(tempFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                if(isNougat())
                {

                    startPhotoZoom(getImageContentUri(tempFile), 150);
                }
                else
                {
                    startPhotoZoom(Uri.fromFile(tempFile), 150);

                }

                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom(data.getData(), 150);
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null)
                    setPicToView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * android7.0之后转换Url方法
     * 转换 content:// uri  *   * @param imageFile  * @return
     */
    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }



    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("return-data", true);

        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);


        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            File f = ImageUtil.createImageFile(getResources().getString(R.string.app_name_en),"Head_",getApplication());
            photoFile = f.getAbsolutePath();
            if(ImageUtil.saveBitmap(photo,f))
            {
                Bitmap backImg = ImageUtil.doBlur(photo,50,false);
                photo = ImageUtil.getRoundedCornerBitmap(photo,photo.getWidth()/2);
                imageHead.setImageBitmap(photo);
//                headBox.setBackground(ImageUtil.toDrawable(backImg));
            }
            else
            {

            }

        }
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {

        return "tmp_head.jpg";
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
