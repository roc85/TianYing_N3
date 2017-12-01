package com.xyxl.tianyingn3.ui.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.common.eventbus.EventBus;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.global.App;
import com.xyxl.tianyingn3.global.FinalDatas;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/11/22 11:58
 * Version : V1.0
 * Introductions : 对话框方法
 */

public class DialogView implements FinalDatas {

    public static void ShowChooseDialog(Context c, List<String> datas, final Handler myHandler)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        String[] strs = new String[datas.size()];
        for(int i=0;i<datas.size();i++)
        {
            strs[i] = datas.get(i);
        }
        builder.setSingleChoiceItems(
                strs,
                0,
                new DialogInterface.OnClickListener()                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //添加操作...
                        Message msg = new Message();
                        msg.what = 1001;
                        msg.arg1 = which;

                        myHandler.sendMessage(msg);
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public static void ShowDialog(Context c, String title, final Handler myHandler) {
        //*******************************//

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        View view = View.inflate(c, R.layout.dialog_confirm, null);
        TextView tvTitle = (TextView)view.findViewById(R.id.textTitle);
        tvTitle.setText(title);
        final Button btnOk = (Button) view.findViewById(R.id.buttonOk);
        final Button btnEsc = (Button) view.findViewById(R.id.buttonEsc);

        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myHandler == null) {
//                    App.getInstance().finishAllActivity();
//                    int nPid = android.os.Process.myPid();
//                    android.os.Process.killProcess(nPid);
//                    System.exit(0);
                    App.finishAllActivity();
                    App.finishCurrentActivity();
                }
            }
        });
        btnEsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


}
