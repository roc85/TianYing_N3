package com.xyxl.tianyingn3.ui.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.ui.customview.MyFragmentPagerAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:44
 * Version : V1.0
 * Introductions : 收发消息列表页面
 */

public class MessageFragment extends BaseFragment {
    private TextView textMsg;
    private TextView textContract;
    private TextView textGroup;
    private LinearLayout titleBox;
    private ViewPager viewFragment;
    private TextView[] titleTexts ;

    private List<Fragment> fragmentList;

    private int currentIndex, nextIndex;
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        textMsg = (TextView)view.findViewById(R.id.textMsg);
        textContract = (TextView)view.findViewById(R.id.textContract);
        textGroup = (TextView)view.findViewById(R.id.textGroup);

        textMsg.setTextColor(0xFFFF0000);
        textContract.setTextColor(0xFF000000);
        textGroup.setTextColor(0xFF000000);

        titleTexts = new TextView[]{textMsg , textContract, textGroup};

        viewFragment = (ViewPager)view.findViewById(R.id.viewFragment);

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new CommunicationFragment());
        fragmentList.add(new ContactFragment());
//        fragmentList.add(new GroupFragment());
//		MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
//				fragmentList);
        Activity a = getHoldingActivity();

        viewFragment.setAdapter(new MyFragmentPagerAdapter(getHoldingActivity().getSupportFragmentManager(), fragmentList));
        viewFragment.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                /**说明：     */
                currentIndex = arg0;
                if(arg0 == 0)
                {
                    textMsg.setTextColor(0xFFFF0000);
                    textContract.setTextColor(0xFF000000);
                    textGroup.setTextColor(0xFF000000);
                }
                if(arg0 == 1)
                {
                    textContract.setTextColor(0xFFFF0000);
                    textMsg.setTextColor(0xFF000000);
                    textGroup.setTextColor(0xFF000000);
                }
//                if(arg0 == 2)
//                {
//                    textGroup.setTextColor(0xFFFF0000);
//                    textContract.setTextColor(0xFF000000);
//                    textMsg.setTextColor(0xFF000000);
//                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                /**说明：     */
                if(arg0<currentIndex)
                {
                    titleTexts[currentIndex].setTextColor(Color.argb(0xFF,(int)(0xFF*arg1),0x00,0x00));
                    titleTexts[arg0].setTextColor(Color.argb(0xFF,(int)(0xFF*(1-arg1)),0x00,0x00));
                }
                else
                {
                    titleTexts[currentIndex].setTextColor(Color.argb(0xFF,(int)(0xFF*(1-arg1)),0x00,0x00));
                    if(currentIndex<1)
                    {
                        titleTexts[currentIndex+1].setTextColor(Color.argb(0xFF,(int)(0xFF*arg1),0x00,0x00));
                    }

                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                /**说明：     */


            }
        });

        textMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textMsg.setTextColor(0xFFFF0000);
                textContract.setTextColor(0xFF000000);
                textGroup.setTextColor(0xFF000000);
                viewFragment.setCurrentItem(0);
                currentIndex = 0;
            }
        });
        textContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textMsg.setTextColor(0xFF000000);
                textContract.setTextColor(0xFFFF0000);
                textGroup.setTextColor(0xFF000000);
                viewFragment.setCurrentItem(1);
                currentIndex = 1;
            }
        });
//        textGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                textMsg.setTextColor(0xFF000000);
//                textContract.setTextColor(0xFFFF0000);
//                textGroup.setTextColor(0xFF000000);
//                viewFragment.setCurrentItem(2);
//                currentIndex = 2;
//            }
//        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_msg;
    }


}
