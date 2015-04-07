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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        String message;
        if (type[position]==1){
            linearLayout.setGravity(Gravity.RIGHT);
            message="You changed "+name[position]+"'s Wallpaper";
        }else{
            linearLayout.setGravity(Gravity.LEFT);
            message=name[position]+" changed your wallpaper";
        }
      Long timeinmilli =Long.parseLong(time[position])*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd , HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeinmilli);
        Date resultdate = cal.getTime();

        TextView tv_name= (TextView) convertView.findViewById(R.id.h_name);
        TextView tv_time= (TextView) convertView.findViewById(R.id.h_time);
        tv_name.setText(message);
        tv_time.setText(sdf.format(resultdate));
        return convertView;
    }
}
