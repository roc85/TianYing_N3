package com.xyxl.tianyingn3.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.BtConnectInfo;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.ui.activities.NewContactActivity;
import com.xyxl.tianyingn3.ui.activities.NewMsgActivity;
import com.xyxl.tianyingn3.ui.customview.IndexChooseView;
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
    private IndexChooseView indexChooseView;

    private ImageView ivNew;

    private List<Fragment> fragmentList;

    private int currentIndex, nextIndex;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_msg;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        textMsg = (TextView)view.findViewById(R.id.textMsg);
        textContract = (TextView)view.findViewById(R.id.textContract);
        textGroup = (TextView)view.findViewById(R.id.textGroup);
        ivNew = (ImageView)view.findViewById(R.id.imageNewMessage);
        indexChooseView = (IndexChooseView)view.findViewById(R.id.indexChooseView);
        indexChooseView.updateDirection(0);

        textMsg.setTextColor(0xff5cca71);
        textContract.setTextColor(0xFFFFFFFF);
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
                    textMsg.setTextColor(0xff5cca71);
                    textContract.setTextColor(0xFFFFFFFF);

                    RelativeLayout.LayoutParams relaParams =
                            new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(18f), getHoldingActivity().getMyDpHeight(19f));
                    relaParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    relaParams.setMargins(0,0,getHoldingActivity().getMyDpWidth(13),0);
                    ivNew.setLayoutParams(relaParams);
                    ivNew.setBackgroundResource(R.drawable.nav_massage_icon_create_massage);

//                    textGroup.setTextColor(0xFF000000);
//                    indexChooseView.updateDirection(0);
                }
                if(arg0 == 1)
                {
                    textContract.setTextColor(0xff5cca71);
                    textMsg.setTextColor(0xFFFFFFFF);

                    RelativeLayout.LayoutParams relaParams =
                            new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(22f), getHoldingActivity().getMyDpHeight(22f));
                    relaParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    relaParams.setMargins(0,0,getHoldingActivity().getMyDpWidth(13),0);
                    ivNew.setLayoutParams(relaParams);
                    ivNew.setBackgroundResource(R.drawable.nav_contact_add);
//                    textGroup.setTextColor(0xFF000000);
//                    indexChooseView.updateDirection(1);

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
//                    titleTexts[currentIndex].setTextColor(Color.argb(0xFF,(int)(0xFF*arg1),0x00,0x00));
//                    titleTexts[arg0].setTextColor(Color.argb(0xFF,(int)(0xFF*(1-arg1)),0x00,0x00));
                    if(arg1<1)
                    {
                        indexChooseView.updateDirection(arg1);

                    }
                }
                else
                {
//                    titleTexts[currentIndex].setTextColor(Color.argb(0xFF,(int)(0xFF*(1-arg1)),0x00,0x00));
//                    if(currentIndex<1)
//                    {
//                        titleTexts[currentIndex+1].setTextColor(Color.argb(0xFF,(int)(0xFF*arg1),0x00,0x00));
//                    }
                    if(arg1 > 0 )
                    {
                        indexChooseView.updateDirection(arg1);

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
                textMsg.setTextColor(0xff5cca71);
                textContract.setTextColor(0xFFFFFFFF);
//                textGroup.setTextColor(0xFF000000);
                viewFragment.setCurrentItem(0);
                currentIndex = 0;
//                indexChooseView.updateDirection(0);
            }
        });
        textContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textMsg.setTextColor(0xFFFFFFFF);
                textContract.setTextColor(0xff5cca71);
//                textGroup.setTextColor(0xFF000000);
                viewFragment.setCurrentItem(1);
                currentIndex = 1;
//                indexChooseView.updateDirection(1);
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

        ivNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex == 0)
                {
                    //新建报文
                    if(BtConnectInfo.getInstance().isConnect() || !TextUtils.isEmpty(BdCardBean.getInstance().getIdNum()))
                    {
                        Intent intent = new Intent(getActivity(), NewMsgActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        ShowToast(getResources().getString(R.string.cant_send_msg));
                    }
                }
                else if(currentIndex == 1)
                {
                    //新建联系人
                    Intent intent = new Intent(getActivity(), NewContactActivity.class);
                    startActivity(intent);
                }
            }
        });

        initUI();
    }


    private void ShowToast(String con)
    {
        Toast.makeText(getActivity(),con,Toast.LENGTH_SHORT).show();

    }

    private void initUI() {
        RelativeLayout.LayoutParams relaParams =
                new RelativeLayout.LayoutParams(getHoldingActivity().getMyDpWidth(18f), getHoldingActivity().getMyDpHeight(19f));
        relaParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relaParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relaParams.setMargins(0,0,getHoldingActivity().getMyDpWidth(13),0);
        ivNew.setLayoutParams(relaParams);
        ivNew.setBackgroundResource(R.drawable.nav_massage_icon_create_massage);
    }


}
