package com.greycodes.zerito.helper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.greycodes.zerito.R;


/**
 * Created by ajmal on 24/2/15.
 */
public class FriendRequestAdapter extends BaseAdapter {
    Context context;
    String[] name,mob;
    int[] id;
    public FriendRequestAdapter(Context context,String[] name,String[] mob,int[] id) {
        this.context=context;
        this.name=name;
        this.mob=mob;
        this.id=id;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.friendrequest_listitem, parent, false);
        }
        TextView tv_name = (TextView) convertView.findViewById(R.id.fr_name);
        TextView tv_mob = (TextView) convertView.findViewById(R.id.fr_mob);
        final TextView tv_accept = (TextView) convertView.findViewById(R.id.fr_accept);
        final TextView tv_reject = (TextView) convertView.findViewById(R.id.fr_reject);

        tv_name.setText(name[position]);
        tv_mob.setText(mob[position]);
        tv_accept.setTag(position);
        tv_reject.setTag(position);

        tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Accpet "+tv_accept.getTag(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context,FriendAcceptService.class);
                intent.putExtra("mob2",mob[((int) tv_accept.getTag())]);
                context.startService(intent);
            }
        });
        tv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Reject  "+tv_reject.getTag(),Toast.LENGTH_LONG).show();
            }
        });
       // /tv.setText(names[position].toString());
       // imageView.setImageResource(images[position]);
        return convertView;
    }
}
