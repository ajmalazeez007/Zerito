package com.greycodes.zerito;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.greycodes.zerito.helper.SendRequestService;


public class NewFriendActivity extends ActionBarActivity {
TextView tv_mob,tv_pin;
    ImageView add;
    String mob,pin;
    //http://ramsandroid4all.blogspot.in/2014/01/display-all-contacts-from-contacts.html
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        tv_mob= (TextView) findViewById(R.id.nf_phoneno);
        tv_pin= (TextView) findViewById(R.id.nf_pin);
        add= (ImageView) findViewById(R.id.nf_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mob= tv_mob.getText().toString();
                pin= tv_pin.getText().toString();

                if(mob.length()<5){
                    Toast.makeText(getApplicationContext(),"Please enter a proper mobile number",Toast.LENGTH_LONG).show();
                }else if (pin.length()!=4){
                    //Toast.makeText(getApplicationContext(),"Mobile number must have 4 digits",Toast.LENGTH_LONG).show();

                }else {
                    Intent service= new Intent(NewFriendActivity.this, SendRequestService.class);
                    service.putExtra("mob1",mob);
                    service.putExtra("pin",pin);
                    startService(service);

                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_friend, menu);
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
