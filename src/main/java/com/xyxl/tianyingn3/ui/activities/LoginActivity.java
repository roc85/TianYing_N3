package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xyxl.tianyingn3.R;

/**
 * Created by Administrator on 2017/11/13 15:30
 * Version : V1.0
 * Introductions : 登录页面
 */
public class LoginActivity extends BaseActivity implements OnClickListener {


    private TextView textTitle;
    private ImageView imageBack;
    private RelativeLayout loginTitle;
    private ImageView imageHeadLogin;
    private EditText editBdNum;
    private EditText editPin;
    private Button buttonlogin;
    private TextView textView4;
    private RelativeLayout line;
    private TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setContentView(R.layout.activity_login);

        initData();
        initView();

    }

    private void initView() {
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setOnClickListener(this);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageBack.setOnClickListener(this);
        loginTitle = (RelativeLayout) findViewById(R.id.loginTitle);
        loginTitle.setOnClickListener(this);
        imageHeadLogin = (ImageView) findViewById(R.id.imageHeadLogin);
        imageHeadLogin.setOnClickListener(this);
        editBdNum = (EditText) findViewById(R.id.editBdNum);
        editBdNum.setOnClickListener(this);
        editPin = (EditText) findViewById(R.id.editPin);
        editPin.setOnClickListener(this);
        buttonlogin = (Button) findViewById(R.id.buttonlogin);
        buttonlogin.setOnClickListener(this);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView4.setOnClickListener(this);
        line = (RelativeLayout) findViewById(R.id.line);
        line.setOnClickListener(this);
        textRegister = (TextView) findViewById(R.id.textRegister);

        textRegister.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonlogin:

                break;
            case R.id.imageBack:
                finish();
                break;
        }
    }

    private void submit() {
        // validate
        String editBdNumString = editBdNum.getText().toString().trim();
        if (TextUtils.isEmpty(editBdNumString)) {
            Toast.makeText(this, "editBdNumString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String editPinString = editPin.getText().toString().trim();
        if (TextUtils.isEmpty(editPinString)) {
            Toast.makeText(this, "editPinString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}

