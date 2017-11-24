package com.xyxl.tianyingn3.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.ui.customview.DragListAdapter;
import com.xyxl.tianyingn3.ui.customview.DragListView;
import com.xyxl.tianyingn3.ui.fragments.GroupFragment;
import com.xyxl.tianyingn3.util.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SetHomeBtnActivity extends BaseActivity {

    private static List<String> list = null;
    private DragListAdapter adapter = null;

    public static List<String> groupKey = new ArrayList<String>();
    private List<String> navList = new ArrayList<String>();
    private List<String> moreList = new ArrayList<String>();
    private TextView textTitle;
    private ImageView imageSave;
    private ImageView imageBack;
    private RelativeLayout titleBox;
    private DragListView dragBtnSetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setContentView(R.layout.activity_set_home_btn);

        initData();

        initView();

    }

    private void initData() {
        list = new ArrayList<String>();

        String homeBtns = SettingSharedPreference.getDataString(this,HOME_BTNS_FLAG);

        if(TextUtils.isEmpty(homeBtns))
        {
            homeBtns = "";
            for(int i=0;i<HOME_BTNS_INFOS.length;i++)
            {
                homeBtns += HOME_BTNS_INFOS[i]+"f"+1+"g";
            }
        }

        try
        {
            String[] homePerBtn = homeBtns.split("g");
            List<Integer> flags = new ArrayList<Integer>();
            for(int i=0;i<homePerBtn.length;i++)
            {
                String[] tmp = homePerBtn[i].split("f");
                if(tmp.length == 2)
                {
                    list.add(tmp[0]);
                    flags.add(CommonUtil.Str2int(tmp[1]));
                }


            }
            adapter = new DragListAdapter(this, list);
            adapter.setFlags(flags);
        }
        catch(Exception e)
        {
            finish();
        }



    }

    private void initView() {
        textTitle = (TextView) findViewById(R.id.textTitle);
        imageSave = (ImageView) findViewById(R.id.imageSave);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        titleBox = (RelativeLayout) findViewById(R.id.titleBox);
        dragBtnSetList = (DragListView) findViewById(R.id.dragBtnSetList);

        dragBtnSetList.setAdapter(adapter);

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] res = adapter.getBtnStr();
                HashMap<String, Integer> flagMap = adapter.getFlagMap();
                String homeBtns = "";
                for(int i=0;i<res.length;i++)
                {
                    homeBtns += res[i]+"f"+flagMap.get(res[i])+"g";
                }
                SettingSharedPreference.setDataString(SetHomeBtnActivity.this,HOME_BTNS_FLAG,homeBtns);
                finish();
            }
        });
    }

}
