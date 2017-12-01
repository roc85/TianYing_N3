package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.xyxl.tianyingn3.global.FinalDatas;

/**
 * Created by Administrator on 2017/11/29 9:38
 * Version : V1.0
 * Introductions :
 */

public class IndexChooseView extends View implements FinalDatas {

    private float startX, boxLength = 25;
    private int chooseColor = 0xff5bc970, backColor = 0xff222222;

    public IndexChooseView(Context context) {
        super(context);
    }

    public IndexChooseView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public IndexChooseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public int getChooseColor() {
        return chooseColor;
    }

    public void setChooseColor(int chooseColor) {
        this.chooseColor = chooseColor;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getBoxLength() {
        return boxLength;
    }

    public void setBoxLength(float boxLength) {
        this.boxLength = boxLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(chooseColor);
        canvas.drawColor(backColor);
        boxLength = getWidth()/2;
        canvas.drawRect(boxLength*startX+boxLength*0.3f , 0, boxLength*startX+boxLength-boxLength*0.3f, getHeight(),p);
    }

    public void updateDirection(float f) {
        startX = f;
        invalidate();
    }
}
