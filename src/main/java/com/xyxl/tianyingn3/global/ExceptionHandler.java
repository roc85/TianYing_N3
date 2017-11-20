package com.xyxl.tianyingn3.global;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/11/16 11:21
 * Version : V1.0
 * Introductions :
 */

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static ExceptionHandler instance = new ExceptionHandler();
    private Context context;
    private String infoPath = "/TYRD_N3_ErrorLog/";
    private Thread.UncaughtExceptionHandler defaultHandler;
    private Map<String, String> devInfos = new HashMap<String, String>();
    private DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    public static ExceptionHandler getInstance() {
        return instance;
    }

    public void setCustomCrashHanler(Context ctx) {
        context = ctx;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * @name uncaughtException(Thread thread, Throwable ex)
     * @description 当发生UncaughtException时会回调此函�?
     * @param thread 发生异常的线�?
     * @param ex 抛出的异�?
     * @return void
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        boolean isDone = doException(ex);
        if (!isDone && defaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处�?
//            App.finishAllActivity();
            defaultHandler.uncaughtException(thread, ex);
        } else {
            // 如果自己处理了异常，则不会弹出错误对话框
//            App.finishAllActivity();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {

            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * @name doException(Throwable ex)
     * @description 处理异常
     * @param ex 抛出的异�?
     * @return 异常处理标志
     */
    private boolean doException(Throwable ex) {
        if (ex == null) {
            return true;
        }

        new Thread() {
            @Override
            public void run() {

                Looper.prepare();
                Toast.makeText(context, "程序出现错误,即将退出", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        collectDeviceInfo(context);
        saveExceptionToFile(ex);
        return true;
    }


    /**
     * @name collectDeviceInfo(Context ctx)
     * @description 收集必须的设备信�?
     * @param ctx
     * @return void
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                devInfos.put("versionName", pi.versionName);
                devInfos.put("versionCode", "" + pi.versionCode);
                devInfos.put("MODEL", "" + Build.MODEL);
                devInfos.put("SDK_INT", "" + Build.VERSION.SDK_INT);
                devInfos.put("PRODUCT", "" + Build.PRODUCT);
                devInfos.put("TIME", "" + getCurrentTime());
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                devInfos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
    }

    /**
     * @name saveExceptionToFile(Throwable ex)
     * @description 保存异常信息到文件中
     * @param ex 抛出的异�?
     * @return void
     */
    private void saveExceptionToFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : devInfos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        try {
            String time = df.format(new Date());
            String fileName = time + ".txt";

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory() + infoPath;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
        } catch (Exception e) {

        }
    }

    /**
     * @name getCurrentTime()
     * @description 获取当前时间
     * @return 当前时间
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String CurrentTime = sdf.format(new Date());
        return CurrentTime;
    }
}
