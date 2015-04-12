package com.greycodes.zerito;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.greycodes.zerito.app.AppController;



/**
 * Created by ajmal on 24/2/15.
 */
public class FriendRequestActivity extends ActionBarActivity{
   static ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendrequest_activity);
        listView= (ListView) findViewById(R.id.fr_listview);
        listView.setAdapter(AppController.friendRequestAdapter);

    }

    public static void updateListView(){
        try {
            listView.setAdapter(AppController.friendRequestAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
