package com.greycodes.zerito.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.greycodes.zerito.R;


/**
 * Created by ajmal on 28/2/15.
 */
public class MyFriendsAdapter extends BaseAdapter {
    Context context;
    int[] id;
    String[] name,mob;
    LayoutInflater inflater;
    public  MyFriendsAdapter(Context context,int[] id,String[] name,String[] mob){
        this.context = context;
        this.id = id;
        this.name = name;
        this.mob = mob;
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
            convertView=inflater.inflate(R.layout.listitem,null);
        }
        TextView tv_name= (TextView) convertView.findViewById(R.id.litsitem_name);
        tv_name.setText(name[position]);
        return convertView;
    }
}
