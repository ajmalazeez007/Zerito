package com.greycodes.zerito;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.greycodes.zerito.helper.FriendRequestService;
import com.greycodes.zerito.helper.MyFriendService;


public class GcmActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm);
        getSupportActionBar().setTitle("Zerito");

    }

    @Override
    protected void onResume() {
        super.onResume();
       int type= getIntent().getIntExtra("type",1);
        Intent intent = new Intent(GcmActivity.this,MyFriendService.class);
        switch (type){

            case 1:
                intent = new Intent(GcmActivity.this, MyFriendService.class);
                startService(intent);
                break;
            case 2:
                intent = new Intent(GcmActivity.this, FriendRequestService.class);
                startService(intent);
                break;
            case 3:
                intent = new Intent(GcmActivity.this, MyFriendService.class);
                startService(intent);
                break;
            case 4:
                intent = new Intent(GcmActivity.this, MyFriendService.class);
                startService(intent);
                break;

        }
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gcm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
