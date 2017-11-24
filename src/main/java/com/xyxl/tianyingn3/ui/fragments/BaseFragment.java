package com.xyxl.tianyingn3.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.ui.activities.BaseActivity;

/**
 * Created by rocgoo on 2017/11/9.
 * Function：
 */

public abstract class BaseFragment extends Fragment implements FinalDatas{

    protected BaseActivity mActivity;

    protected abstract void initView(View view, Bundle savedInstanceState);

    //获取布局文件ID
    protected abstract int getLayoutId();

    //获取宿主Activity
    protected BaseActivity getHoldingActivity() {
        if(mActivity == null)
        {
            this.mActivity = (BaseActivity) getActivity();
        }
        return mActivity;
    }

    @Override
    public void onAttach(Context context) {//Modified 2016-06-01</span>
        super.onAttach(context);
        this.mActivity = (BaseActivity) getActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        initView(view, savedInstanceState);
        return view;
    }
}

