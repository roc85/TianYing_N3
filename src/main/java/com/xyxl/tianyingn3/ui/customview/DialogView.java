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

    public static void ShowCHooseDialog(Context c, List<String> datas, final Handler myHandler)
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

    public static void ShowDialog(Context c, String title, String msg,
                                  String btn1, String btn2, final Handler myHandler) {
        //*******************************//

//        AlertDialog.Builder builder = new AlertDialog.Builder(c);
//        View view = View.inflate(c, R.layout.time_dialog, null);
//        final DatePicker datePicker = (DatePicker) view
//                .findViewById(R.id.datePicker1);
//        final TimePicker timePicker = (android.widget.TimePicker) view
//                .findViewById(R.id.timePicker1);
//        builder.setView(view);
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(System.currentTimeMillis());
//        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH), null);
//
//        timePicker.setIs24HourView(true);
//        timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
//        timePicker.setCurrentMinute(Calendar.MINUTE);
//
//        builder.setTitle("Choose date & time");
//        builder.setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        StringBuffer sb = new StringBuffer();
//                        sb.append(String.format("%d-%02d-%02d",
//                                datePicker.getYear(),
//                                datePicker.getMonth() + 1,
//                                datePicker.getDayOfMonth()));
//                        sb.append(" ");
//
//                        sb.append(timePicker.getCurrentHour()).append(":")
//                                .append(timePicker.getCurrentMinute());
//
//                        if (sb != null && sb.length() > 0) {
//                            Message msg = new Message();
//                            msg.what = 101;
//                            msg.obj = sb.toString();
//                            myHandler.sendMessage(msg);
//                        }
//
//                        dialog.cancel();
//                    }
//                });
//
//        Dialog dialog = builder.create();
//        dialog.show();
    }


}
