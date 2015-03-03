package com.greycodes.zerito;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.greycodes.zerito.app.AppController;
import com.greycodes.zerito.helper.FriendRequestService;



public class HomeActivity extends ActionBarActivity {
ListView listView;
    ImageView add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listView = (ListView) findViewById(R.id.home_listview);
        add = (ImageView) findViewById(R.id.home_addfirend);
        listView.setAdapter(AppController.myFriendsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),AppController.name[position],Toast.LENGTH_LONG).show();
                AppController.selectedid=AppController.id[position];
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this,NewFriendActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_fr) {
            Toast.makeText(getApplicationContext(),"Friend request",Toast.LENGTH_LONG).show();
            startService(new Intent(HomeActivity.this, FriendRequestService.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
