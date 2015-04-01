package com.greycodes.zerito;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.greycodes.zerito.helper.SignInService;


public class SignInActivity extends ActionBarActivity {
    EditText etmob,etpin;
    ImageView submit;
    String mob,pin;
    String SENDER_ID = "187588160331";
    public static final String PROPERTY_REG_ID = "registration_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        try {
            etmob =(EditText)findViewById(R.id.phone);
            etpin =(EditText)findViewById(R.id.pin);
            submit = (ImageView) findViewById(R.id.submit);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mob= etmob.getText().toString();
                    pin= etpin.getText().toString();
                    if(mob.length()<5){
                        Toast.makeText(getApplicationContext(), "Mobile Number should have atleast 5 digit " , Toast.LENGTH_LONG).show();

                    } else if (pin.length()!=4) {
                        Toast.makeText(getApplicationContext(), "Pin should be  4 digit " , Toast.LENGTH_LONG).show();

                    }else{
                        Intent intent =new Intent(SignInActivity.this, SignInService.class);
                        intent.putExtra("mob",mob);
                        intent.putExtra("pin",pin);
                        startService(intent);

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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
