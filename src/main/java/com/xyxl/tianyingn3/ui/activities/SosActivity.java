package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.ui.customview.DialogView;

/**
 * Created by Administrator on 2017/11/13 15:30
 * Version : V1.0
 * Introductions : SOS页面
 */
public class SosActivity extends BaseActivity {

    private ImageView imageSos;
    private TextView textSos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setContentView(R.layout.activity_sos);
        initView();


    }

    private void initView() {
        imageSos = (ImageView) findViewById(R.id.imageSos);
        textSos = (TextView) findViewById(R.id.textSos);

        imageSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView.ShowDialog(SosActivity.this,getResources().getString(R.string.sos_confirm),null);
            }
        });

        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(getMyDpWidth(259), getMyDpHeight(259));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btnParams.setMargins(0,getMyDpHeight(135),0,0);
        imageSos.setLayoutParams(btnParams);
    }
}
