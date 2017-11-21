package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.database.Contact_DB;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 新建联系人页面
 */
public class NewContactActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edName;
    private EditText edNum;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contract);
        initView();
    }

    private void initView() {
        edName = (EditText) findViewById(R.id.edName);
        edNum = (EditText) findViewById(R.id.edNum);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:

                Contact_DB t = new Contact_DB();
                t.setBdNum(BdCardBean.FormatCardNum(edNum.getText().toString()));
                t.setContactName(edName.getText().toString());
                t.setContactOwner("");
                t.setContactType(0);
                t.setHead("");
                t.setPassword("");
                t.setPhone("");
                t.setRemark("");
                t.save();

                finish();
                break;
        }
    }

    private void submit() {
        // validate
        String edNameString = edName.getText().toString().trim();
        if (TextUtils.isEmpty(edNameString)) {
            Toast.makeText(this, "name", Toast.LENGTH_SHORT).show();
            return;
        }

        String edNumString = edNum.getText().toString().trim();
        if (TextUtils.isEmpty(edNumString)) {
            Toast.makeText(this, "num", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
