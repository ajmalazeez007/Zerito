package com.greycodes.zerito.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greycodes.zerito.R;

/**
 * Created by ajmal on 31/3/15.
 */
public class HistoryAdapter extends BaseAdapter {
    String[] name,time;
    int[] type;
    Context   context;
    LayoutInflater inflater;
    public HistoryAdapter(Context context,String[] name,String[] time,int[] type){
        this.context=context;
        this.name=name;
        this.time=time;
        this.type=type;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=inflater.inflate(R.layout.history_listitem,null);
        }
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.h_lineallayout);
        if (type[position]==1){
            linearLayout.setGravity(Gravity.LEFT);
        }else{
            linearLayout.setGravity(Gravity.RIGHT);
        }
        TextView tv_name= (TextView) convertView.findViewById(R.id.h_name);
        TextView tv_time= (TextView) convertView.findViewById(R.id.h_time);
        tv_name.setText(name[position]);
        tv_time.setText(time[position]);
        return convertView;
    }
}
