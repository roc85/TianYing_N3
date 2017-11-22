package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;

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

            }
        });
    }
}
