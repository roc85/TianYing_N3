package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.database.Notice_DB;
import com.xyxl.tianyingn3.global.FinalDatas;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/11/24 15:19
 * Version : V1.0
 * Introductions :
 */

public class DragListAdapter extends ArrayAdapter<String> implements FinalDatas {

    private List<String> btnList;
    private List<Integer> iconList;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<Integer> flags;

    private String[] btnStr;
//    private int[] flagIndex;

    private HashMap<String, Integer> flagMap = new HashMap<String, Integer>();

    static class ViewHolder
    {
        public ImageView imgIcon, imgDrag;
        public TextView tvCon;
        private CheckBox checkShow;
    }
    ViewHolder viewHolder = null;

    public DragListAdapter(Context context, List<String> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.btnList = objects;
        btnStr = new String[btnList.size()];
//        flagIndex = new int[btnList.size()];

        mInflater = LayoutInflater.from(context);
    }

    public List<String> getList(){
        return btnList;
    }

    public List<Integer> getFlags() {
        return flags;
    }

    public void setFlags(List<Integer> flags) {
        this.flags = flags;
        for(int i=0;i<flags.size();i++)
        {
//            flagIndex[i] = flags.get(i);
            flagMap.put(btnList.get(i),flags.get(i));
        }

    }

    public HashMap<String, Integer> getFlagMap() {
        return flagMap;
    }

    public void setFlagMap(HashMap<String, Integer> flagMap) {
        this.flagMap = flagMap;
    }

    public String[] getBtnStr() {
        for (int i = 0; i < getCount(); i++) {
            btnStr[i] = getItem(i);
        }
        return btnStr;
    }

    public void setBtnStr(String[] btnStr) {
        this.btnStr = btnStr;
    }

//    public int[] getFlagIndex() {
//        return flagIndex;
//    }
//
//    public void setFlagIndex(int[] flagIndex) {
//        this.flagIndex = flagIndex;
//    }

//    public void ExchangeFlagInt(int i1, int i2)
//    {
//        int tmp = flagIndex[i1];
//        flagIndex[i1] = flagIndex[i2];
//        flagIndex[i2] = tmp;
//
//    }

    @Override
    public int getCount() {
        return btnList.size();
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(
                R.layout.drag_list_item, null);

        viewHolder = new ViewHolder();
        viewHolder.imgIcon = (ImageView) convertView
                .findViewById(R.id.drag_list_icon);
        viewHolder.imgDrag = (ImageView) convertView
                .findViewById(R.id.drag_list_item_image);

        viewHolder.tvCon = (TextView) convertView
                .findViewById(R.id.drag_list_item_text);
        viewHolder.checkShow = (CheckBox) convertView
                .findViewById(R.id.checkShowBtn);
        viewHolder.checkShow.setTag(btnList.get(position));
        viewHolder.checkShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flagMap.remove(buttonView.getTag());
                if (isChecked) {

                    flagMap.put((String) buttonView.getTag(), 1);
                } else {
                    flagMap.put((String) buttonView.getTag(), 0);
                }


            }
        });


        viewHolder.tvCon.setText(btnList.get(position));


        if (flagMap.get(btnList.get(position)) == 1) {
            viewHolder.checkShow.setChecked(true);
        } else {
            viewHolder.checkShow.setChecked(false);
        }

        return convertView;
    }
}
