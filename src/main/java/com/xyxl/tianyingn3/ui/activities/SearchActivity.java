package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ExpandableListView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.SearchResultsBean;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.ui.customview.ClearEditText;
import com.xyxl.tianyingn3.ui.customview.SearchResultAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 搜索页面
 */
public class SearchActivity extends BaseActivity {

    private ClearEditText mClearEditText;
    private ExpandableListView resultList;
    private SearchResultAdapter searchResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setContentView(R.layout.activity_search);

        initView();
    }

    private void initView() {
        mClearEditText = (ClearEditText) findViewById(R.id.search_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                if(!TextUtils.isEmpty(s.toString()))
                {
                    filterData(s.toString());
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        resultList = (ExpandableListView)findViewById(R.id.resultList);


    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {

        List<String> groupList = new ArrayList<String>();

        List<List<SearchResultsBean>> childList = new ArrayList<List<SearchResultsBean>>();

        List<Contact_DB> contactList = Contact_DB.find(Contact_DB.class,"contact_Name LIKE ? OR bd_Num LIKE ?","%"+filterStr+"%","%"+filterStr+"%");
        if(contactList.size()>0)
        {
            groupList.add("联系人");
            List<SearchResultsBean> cList = new ArrayList<SearchResultsBean>();
            for (int i=0; i<contactList.size(); i++)
            {
                cList.add(new SearchResultsBean(0,contactList.get(i).getContactName(),contactList.get(i).getBdNum(),contactList.get(i).getId()));
            }
            childList.add(cList);
        }
        List<Message_DB> messageList = Message_DB.find(Message_DB.class,"msg_Con LIKE ?","%"+filterStr+"%");
        if(messageList.size()>0)
        {
            groupList.add("报文信息");
            List<SearchResultsBean> mList = new ArrayList<SearchResultsBean>();
            for (int i=0; i<messageList.size(); i++)
            {
                mList.add(new SearchResultsBean(1,messageList.get(i).getRcvUserName(),messageList.get(i).getMsgCon(),messageList.get(i).getId()));
            }
            childList.add(mList);
        }

        searchResultAdapter = new SearchResultAdapter(this, groupList, childList);
        resultList.setAdapter(searchResultAdapter);

        int groupCount = resultList.getCount();

        for (int i=0; i<groupCount; i++) {

            resultList.expandGroup(i);

        };
    }
}
