package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/11/27 16:49
 * Version : V1.0
 * Introductions :
 */

public class BeamsView extends View {

    private int[] beamLvs ;
    private static int COLOR_USE = 0xff68e580;
    private static int COLOR_UNUSE = 0xffdcdcdc;
    public BeamsView(Context context) {
        super(context);
    }

    public BeamsView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public BeamsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public int[] getBeamLvs() {
        return beamLvs;
    }

    public void setBeamLvs(int[] beamLvs) {
        this.beamLvs = beamLvs;
    }

    @Override
    protected void onDraw(Canvas canvas) {


        if (beamLvs == null) {
            beamLvs = new int[10];
        }

        int beamWidth = getWidth()/(beamLvs.length*2+1);
        Paint usePaint = new Paint();
        usePaint.setColor(COLOR_USE);
        Paint unUsePaint = new Paint();
        unUsePaint.setColor(COLOR_UNUSE);
        for(int i=0;i<beamLvs.length;i++)
        {
            canvas.drawRect(beamWidth*(i*2+1),getHeight()*0.05f,beamWidth*(i*2+2), (float) (getHeight()*0.05+getHeight()*0.9f*(4-beamLvs[i])/4),unUsePaint);
            canvas.drawRect(beamWidth*(i*2+1),(float) (getHeight()*0.05+getHeight()*0.9f*(4-beamLvs[i])/4),beamWidth*(i*2+2), getHeight()*0.95f,usePaint);

        }
//        canvas.restore();
    }

    public void updateDirection(int[] b) {
        beamLvs = b;
        invalidate();
    }
}
