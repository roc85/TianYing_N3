package com.xyxl.tianyingn3.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.ui.activities.NewContactActivity;
import com.xyxl.tianyingn3.ui.customview.ClearEditText;
import com.xyxl.tianyingn3.ui.customview.WaveSideBar;
import com.xyxl.tianyingn3.util.PinyinComparator;
import com.xyxl.tianyingn3.util.PinyinUtils;
import com.xyxl.tianyingn3.ui.customview.SortAdapter;
import com.xyxl.tianyingn3.bean.SortModel;
import com.xyxl.tianyingn3.util.TitleItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 联系人列表页面
 */

public class ContactFragment extends Fragment implements FinalDatas {

    private RecyclerView mRecyclerView;
    private WaveSideBar mSideBar;
    private SortAdapter mAdapter;
    private ClearEditText mClearEditText;
    private LinearLayoutManager manager;

    private List<SortModel> mDateList;
    private TitleItemDecoration mDecoration;

    private List<Contact_DB> contactDbList;
    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private PinyinComparator mComparator;

    private ImageView ivAddContact;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:

                    break;

                case 101:

                    break;
                default:
                    break;
            }
        };
    };

    private void initView(View view) {

        mComparator = new PinyinComparator();

        mSideBar = (WaveSideBar) view.findViewById(R.id.sideBar);

        //设置右侧SideBar触摸监听
        mSideBar.setOnTouchLetterChangeListener(new WaveSideBar.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(letter.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);



        // 根据a-z进行排序源数据
        Collections.sort(mDateList, mComparator);

        //RecyclerView设置manager
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new SortAdapter(getActivity(), mDateList);
        mRecyclerView.setAdapter(mAdapter);
        mDecoration = new TitleItemDecoration(getActivity(), mDateList);
        //如果add两个，那么按照先后顺序，依次渲染。
        mRecyclerView.addItemDecoration(mDecoration);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //添加联系人按钮
//        ivAddContact = (ImageView)view.findViewById(R.id.addContact);
//        ivAddContact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), NewContactActivity.class);
//                startActivity(intent);
//
//            }
//        });
    }


    private void initData() {
        contactDbList = Contact_DB.listAll(Contact_DB.class);
        mDateList = filledData(contactDbList);

    }

    /**
     * 为RecyclerView填充数据
     *
     * @param list
     * @return
     */
    private List<SortModel> filledData(List<Contact_DB> list) {
        List<SortModel> mSortList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(list.get(i).getContactName());
            sortModel.setNum(list.get(i).getBdNum());
            sortModel.setHeadFile(list.get(i).getHead());
            sortModel.set_id(list.get(i).getId());
            LogUtil.i(sortModel.get_id()+"-ID_CONTACT");

            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(list.get(i).getContactName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setLetters(sortString.toUpperCase());
            } else {
                sortModel.setLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = filledData(contactDbList);
        } else {
            filterDateList.clear();
            for (SortModel sortModel : mDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 ||
                        PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                        //不区分大小写
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                        ) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, mComparator);
        mDateList.clear();
        mDateList.addAll(filterDateList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_contract, container, false);

        initData();

        initView(view);

        return view;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        contactDbList = Contact_DB.listAll(Contact_DB.class);
        List<SortModel> filterDateList = filledData(contactDbList);
        Collections.sort(filterDateList, mComparator);
        mDateList.clear();
        mDateList.addAll(filterDateList);
        mAdapter.notifyDataSetChanged();

        super.onResume();
    }

    @Subscribe
    public void setContent(Contact_DB data) {

        contactDbList = Contact_DB.listAll(Contact_DB.class);
        List<SortModel> filterDateList = filledData(contactDbList);
        Collections.sort(filterDateList, mComparator);
        mDateList.clear();
        mDateList.addAll(filterDateList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册到bus事件总线中

        AppBus.getInstance().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        AppBus.getInstance().unregister(this);
    }
}
