package com.xyxl.tianyingn3.util;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/11/22 15:33
 * Version : V1.0
 * Introductions : Android 图片工具ImageUtil
 裁图
 Bitmap圆角
 缩略图
 视频缩略图
 各种类型转换
 */

public class ImageUtil {
    private static final String TAG = "ImageUtil";

    public static final int ACTION_SET_AVATAR = 260;
    public static final int ACTION_SET_COVER = 261;
    public static final int ACTION_TAKE_PIC = 262;
    public static final int ACTION_TAKE_PIC_FOR_GRIDVIEW = 263;
    public static final int ACTION_PICK_PIC = 264;
    public static final int ACTION_ACTION_CROP = 265;

    /**
     * 调用系统自带裁图工具
     *
     * @param activity
     * @param size
     * @param uri
     * @param action
     * @param cropFile
     */
    public static void cropPicture(Activity activity, int size, Uri uri, int action, File cropFile) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // 返回格式
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", size);
            intent.putExtra("outputY", size);
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("cropIfNeeded", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropFile));
            activity.startActivityForResult(intent, action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用系统自带裁图工具
     * outputX = 250
     * outputY = 250
     *
     * @param activity
     * @param uri
     * @param action
     * @param cropFile
     */
    public static void cropPicture(Activity activity, Uri uri, int action, File cropFile) {
        cropPicture(activity, 250, uri, action, cropFile);
    }

    /**
     * 调用系统自带裁图工具
     * 并保存文件
     * outputX = 250
     * outputY = 250
     *
     * @param activity
     * @param uri
     * @param action
     * @param appName
     * @param application
     * @return
     */
    public static File cropPicture(Activity activity, Uri uri, int action, String appName, Application application) {
        File resultFile = createImageFile(appName,"", application);
        cropPicture(activity, 250, uri, action, resultFile);
        return resultFile;
    }

    /**
     * 创建图片文件
     *
     * @param appName
     * @param application
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static File createImageFile(String appName, String fileName, Application application) {
        File folder = createImageFileInCameraFolder(appName, application);
        String filename = fileName +"_"+ new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        return new File(folder, filename + ".jpg");
    }

    /**
     * Save bitmap.
     */
    public static boolean saveBitmap(Bitmap src, File f) {
        Log.e(TAG, "保存图片");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            src.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 创建图片文件夹
     *
     * @param appName
     * @param application
     * @return
     */
    public static File createImageFileInCameraFolder(String appName, Application application) {
        String folder = ImageUtil.createAPPFolder(appName, application);
        File file = new File(folder, "image");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }



    /**
     * 创建App文件夹
     *
     * @param appName
     * @param application
     * @return
     */
    public static String createAPPFolder(String appName, Application application) {
        return createAPPFolder(appName, application, null);
    }

    /**
     * 创建App文件夹
     *
     * @param appName
     * @param application
     * @param folderName
     * @return
     */
    public static String createAPPFolder(String appName, Application application, String folderName) {
        File root = Environment.getExternalStorageDirectory();
        File folder;
        /**
         * 如果存在SD卡
         */
        if (DeviceUtil.isSDCardAvailable() && root != null) {
            folder = new File(root, appName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } else {
            /**
             * 不存在SD卡，就放到缓存文件夹内
             */
            root = application.getCacheDir();
            folder = new File(root, appName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        if (folderName != null) {
            folder = new File(folder, folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        return folder.getAbsolutePath();
    }


    //获得圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    //获得带倒影的图片方法
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap){
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,
                0, height/2, width, height/2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height,width,height + reflectionGap,
                deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                bitmap.getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 图片解析
     *
     * @param path
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap decodeScaleImage(String path, int targetWidth, int targetHeight) {
        BitmapFactory.Options bitmapOptions = getBitmapOptions(path);
        bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions, targetWidth, targetHeight);
        bitmapOptions.inJustDecodeBounds = false;
        Bitmap noRotatingBitmap = BitmapFactory.decodeFile(path, bitmapOptions);
        int degree = readPictureDegree(path);
        Bitmap rotatingBitmap;
        if (noRotatingBitmap != null && degree != 0) {
            rotatingBitmap = rotatingImageView(degree, noRotatingBitmap);
            noRotatingBitmap.recycle();
            return rotatingBitmap;
        } else {
            return noRotatingBitmap;
        }
    }


    /**
     * 获取缩略图
     *
     * @param path
     * @param targetWidth
     * @return
     */
    public static String getThumbnailImage(String path, int targetWidth) {
        Bitmap scaleImage = decodeScaleImage(path, targetWidth, targetWidth);
        try {
            File file = File.createTempFile("image", ".jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            scaleImage.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream);
            fileOutputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return path;
        }
    }

    /**
     * 图片解析
     *
     * @param context
     * @param resId
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap decodeScaleImage(Context context, int resId, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        return bitmap;
    }

    /**
     * 计算样本大小
     *
     * @param options
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int targetWidth, int targetHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int scale = 1;
        if (height > targetHeight || width > targetWidth) {
            int heightScale = Math.round((float) height / (float) targetHeight);
            int widthScale = Math.round((float) width / (float) targetWidth);
            scale = heightScale > widthScale ? heightScale : widthScale;
        }
        return scale;
    }


    /**
     * 获取BitmapFactory.Options
     *
     * @param pathName
     * @return
     */
    public static BitmapFactory.Options getBitmapOptions(String pathName) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, opts);
        return opts;
    }

    /**
     * 获取图片角度
     *
     * @param filename
     * @return
     */
    public static int readPictureDegree(String filename) {
        short degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filename);
            int anInt = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (anInt) {
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                case ExifInterface.ORIENTATION_TRANSPOSE:
                case ExifInterface.ORIENTATION_TRANSVERSE:
                default:
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return degree;
    }

    /**
     * 旋转ImageView
     *
     * @param degree
     * @param source
     * @return
     */
    public static Bitmap rotatingImageView(int degree, Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) degree);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }




    /**
     * 获取视频缩略图
     *
     * @param filePath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    public static Bitmap getVideoThumbnail(String filePath, int width, int height, int kind) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 保存视频缩略图
     *
     * @param file
     * @param path 路径
     * @param width
     * @param height
     * @param kind
     * @return
     */
    public static String saveVideoThumbnail(File file, String path, int width, int height, int kind) {
        Bitmap videoThumbnail = getVideoThumbnail(file.getAbsolutePath(), width, height, kind);
        File thumbFile = new File(path, "th" + file.getName());
        try {
            thumbFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(thumbFile);
        } catch (FileNotFoundException var10) {
            var10.printStackTrace();
        }
        videoThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        try {
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileOutputStream.close();
        } catch (IOException var8) {
            var8.printStackTrace();
        }
        return thumbFile.getAbsolutePath();
    }

    /**
     * Image resource ID was converted into a byte [] data
     * 图片资源ID 转换 为 图片 byte[] 数据
     *
     * @param context
     * @param imageResourceId
     * @return
     */
    public static byte[] toByteArray(Context context, int imageResourceId) {
        Bitmap bitmap = ImageUtil.toBitmap(context, imageResourceId);
        if (bitmap != null) {
            return ImageUtil.toByteArray(bitmap);
        } else {
            return null;
        }
    }

    /**
     * ImageView getDrawable () into a byte [] data
     * ImageView的getDrawable() 转换为 byte[] 数据
     *
     * @param imageView
     * @return
     */
    public static byte[] toByteArray(ImageView imageView) {
        Bitmap bitmap = ImageUtil.toBitmap(imageView);
        if (bitmap != null)
            return ImageUtil.toByteArray(bitmap);
        else {
            Log.w(ImageUtil.TAG,
                    "the ImageView imageView content was invalid");
            return null;
        }
    }

    /**
     * byte [] data type conversion for Bitmap data types
     * byte[]数据类型转换为 Bitmap数据类型
     *
     * @param imageData
     * @return
     */
    public static Bitmap toBitmap(byte[] imageData) {
        if ((imageData != null) && (imageData.length != 0)) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0,
                    imageData.length);
            return bitmap;
        } else {
            Log.w(ImageUtil.TAG, "the byte[] imageData content was invalid");
            return null;
        }
    }

    /**
     * Image resource ID is converted to Bitmap type data
     * 资源ID 转换为 Bitmap类型数据
     *
     * @param context
     * @param imageResourceId
     * @return
     */
    public static Bitmap toBitmap(Context context, int imageResourceId) {
        // 将图片转化为位图
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                imageResourceId);
        if (bitmap != null) {
            return bitmap;
        } else {
            Log.w(ImageUtil.TAG,
                    "the int imageResourceId content was invalid");
            return null;
        }
    }

    /**
     * ImageView types into a Bitmap
     * ImageView类型转换为Bitmap
     *
     * @param imageView
     * @return
     */
    public static Bitmap toBitmap(ImageView imageView) {
        if (imageView.getDrawable() != null) {
            Bitmap bitmap = ImageUtil.toBitmap(imageView.getDrawable());
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * Bitmap type is converted into a image byte [] data
     * Bitmap类型 转换 为图片 byte[] 数据
     *
     * @param bitmap
     * @return
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;
            // 创建一个字节数组输出流，流的大小为size
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    byteArrayOutputStream);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imageData = byteArrayOutputStream.toByteArray();
            return imageData;
        } else {
            Log.w(ImageUtil.TAG,
                    "the Bitmap bitmap content was invalid");
            return null;
        }

    }

    /**
     * Drawable type into a Bitmap
     * Drawable 类型转换为 Bitmap类型
     *
     * @param drawable
     * @return
     */
    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            return bitmap;
        } else {
            Log.w(ImageUtil.TAG,
                    "the Drawable drawable content was invalid");
            return null;
        }
    }

    /**
     * Bitmap type into a Drawable
     * Bitmap 类型转换为 Drawable类型
     *
     * @param bitmap
     * @return
     */
    public static Drawable toDrawable(Bitmap bitmap) {
        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(bitmap);
            return drawable;
        } else {
            Log.w(ImageUtil.TAG,
                    "the Bitmap bitmap content was invalid");
            return null;
        }
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at="" quasimondo.com="">
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at="" kayenko.com="">
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

}
