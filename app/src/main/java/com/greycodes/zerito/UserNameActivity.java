package com.greycodes.zerito;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.greycodes.zerito.service.UpdateUsernameService;


public class UserNameActivity extends ActionBarActivity {
    EditText etusername;
    ImageView btsubmit;
     Context context;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usernamelayout);
        etusername= (EditText) findViewById(R.id.ul_username);
        btsubmit= (ImageView) findViewById(R.id.ul_submit);
        try {
            name=getIntent().getStringExtra("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        etusername.setText(name);

        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=etusername.getText().toString();
                if (name.length()>3){
                    Intent intent = new Intent(UserNameActivity.this,UpdateUsernameService.class);
                    intent.putExtra("name",name);
                    startService(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Name should have atleast 3 characters ",Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_name, menu);
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
