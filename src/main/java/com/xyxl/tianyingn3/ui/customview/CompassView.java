package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CompassView extends android.support.v7.widget.AppCompatImageView {
    private float mDirection;
    private Drawable compass;
    private Bitmap imgCompass;

    public CompassView(Context context) {
        super(context);
        mDirection = 0.0f;
        compass = null;
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDirection = 0.0f;
        compass = null;
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDirection = 0.0f;
        compass = null;
    }

    public void setBitmap(Bitmap img)
    {
        this.imgCompass = img;
    }
    @Override
    protected void onDraw(Canvas canvas) {


        if (compass == null) {
            compass = getDrawable();
            compass.setBounds(0, 0, getWidth(), getHeight());
        }

        canvas.save();
        canvas.rotate(mDirection, getWidth() / 2, getHeight() / 2);
        if(imgCompass != null)
        {
            canvas.drawBitmap(imgCompass,null,new Rect(0,0,getWidth(),getHeight()),null);
        }

//        compass.draw(canvas);
        canvas.restore();
    }

    public void updateDirection(float direction) {
        mDirection = direction;
        invalidate();
    }

}
