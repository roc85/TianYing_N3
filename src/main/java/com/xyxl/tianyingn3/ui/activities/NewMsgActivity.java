package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bluetooth.BtSendDatas;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.TestMsg;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 新建报文页面
 */
public class NewMsgActivity extends BaseActivity implements View.OnClickListener {

    private EditText editText;
    private EditText editText2;
    private Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg);
        initView();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        button3 = (Button) findViewById(R.id.button3);

        button3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button3:
                AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(editText.getText().toString(), 1, 2, editText2.getText().toString()), null));
                break;
        }
    }

    private void submit() {
        // validate
        String editTextString = editText.getText().toString().trim();
        if (TextUtils.isEmpty(editTextString)) {
            Toast.makeText(this, "editTextString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String editText2String = editText2.getText().toString().trim();
        if (TextUtils.isEmpty(editText2String)) {
            Toast.makeText(this, "editText2String不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
