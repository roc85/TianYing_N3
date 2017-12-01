package com.xyxl.tianyingn3.ui.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.global.App;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.ui.customview.DialogView;
import com.xyxl.tianyingn3.ui.fragments.BaseFragment;
import com.xyxl.tianyingn3.global.FinalDatas;

/**
 * The type Base activity.
 */
public abstract class BaseActivity extends AppCompatActivity implements FinalDatas{

    //返回键退出标识 0-正常退出 1-对话框退出 2-两次back退出
    private int exitFlag = 0;
    //屏幕宽高数据
    private int screenWidth, screenHeight;

    private float sDpi;
    //application实例
    private App myApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        App.pushActivity(this);
        //通过dpi进行适配调节
//        sDpi = getScreenDpi()>1?(getScreenDpi()/1.5f):(getScreenDpi());
        sDpi = 1;//getScreenDpi();//
        screenHeight = getScreenHeight();
        screenWidth = getScreenWidth();
        LogUtil.e("DPI-"+sDpi+" H-"+screenHeight+" W-"+screenWidth);
//        myApp = (App) getApplication();

    }

    public int getScreenWidth() {
        //获取屏幕宽高
        WindowManager manager = this.getWindowManager();

        DisplayMetrics outMetrics = new DisplayMetrics();

        manager.getDefaultDisplay().getMetrics(outMetrics);

        screenWidth = outMetrics.widthPixels;

        return screenWidth;
    }

    public int getMyDpHeight(float src)
    {
        int res = 0;
        res = (int) (src * screenHeight/640*sDpi);
        return res;
    }

    public int getMyDpWidth(float src)
    {
        int res = 0;
        res = (int) (src * screenWidth/360*sDpi);
        return res;
    }

    public float getScreenDpi()
    {
        WindowManager manager = this.getWindowManager();

        DisplayMetrics outMetrics = new DisplayMetrics();

        manager.getDefaultDisplay().getMetrics(outMetrics);

        return outMetrics.density;
    }

    public int getScreenHeight() {
        //获取屏幕宽高
        WindowManager manager = this.getWindowManager();

        DisplayMetrics outMetrics = new DisplayMetrics();

        manager.getDefaultDisplay().getMetrics(outMetrics);

        screenHeight = outMetrics.heightPixels;

        return screenHeight;
    }


    public int getExitFlag() {
        return exitFlag;
    }

    /**
     * Sets exit flag.
     *
     * @param exitFlag the exit flag 0-正常退出 1-对话框退出
     */
    public void setExitFlag(int exitFlag) {
        this.exitFlag = exitFlag;
    }

    /**
     * Open activity.
     *
     * @param esc the esc 是否退出当前Activity
     * @param des the des
     */
    public void OpenActivity(boolean esc, Class des)
    {
        Intent intent = new Intent(this,des);
        startActivity(intent);
        if(esc)
        {
            finish();
        }
    }

    /**
     * Show toast.
     *
     * @param text the text
     */
    public void ShowToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        App.finishActivity(this);

    }
    //返回键返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if(exitFlag == 1)
            {
                showExitAlert();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitAlert() {
        AlertDialog.Builder b=new AlertDialog.Builder(this).setTitle("是否退出？");

        b
                //.setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作

//                        App.finishAllActivity();
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.cancelAll();
                        int nPid = android.os.Process.myPid();
                        android.os.Process.killProcess(nPid);

                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作

                    }
                });
        b.show();

//        DialogView.ShowDialog(BaseActivity.this,getResources().getString(R.string.exit_confirm),null);
    }
}
