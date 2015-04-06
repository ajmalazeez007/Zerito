package com.greycodes.zerito;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.greycodes.zerito.helper.RegisterService;


public class VerifyActivity extends ActionBarActivity {
    Intent intent;
    SharedPreferences sharedPreferences;
    String mob,message,pin;
    IntentFilter filter;
    private BroadcastReceiver receiver;
    static final String ACTION ="android.provider.Telephony.SMS_RECEIVED";
    TextView textView;
    EditText etpin;
    Button btverify;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        sharedPreferences= getSharedPreferences("zerito", Context.MODE_PRIVATE);
        textView= (TextView) findViewById(R.id.verify_tv);
        etpin= (EditText) findViewById(R.id.verify_pin);
        btverify= (Button) findViewById(R.id.verify_bt);

        mob=sharedPreferences.getString("mobnum", "");
        pin=sharedPreferences.getString("pin", "");

        filter  = new IntentFilter();
        filter.addAction(ACTION);
        filter.setPriority(999);
        message="Welcome to Zeito.Your confirmation number is  "+pin;

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub
                if(arg1.getAction().equalsIgnoreCase(ACTION))
                {
                    Bundle extras = arg1.getExtras();

                    String strMessage ;
                    if ( extras != null )
                    {
                        Object[] smsextras = (Object[]) extras.get( "pdus" );

                        for ( int i = 0; i < smsextras.length; i++ )
                        {
                            SmsMessage smsmsg = SmsMessage.createFromPdu((byte[]) smsextras[i]);
                            String strMsgBody="";
                            strMsgBody = smsmsg.getMessageBody().toString();
                            String strMsgSrc = smsmsg.getOriginatingAddress();

                            if(strMsgSrc.equals(mob))
                            {

                                if (strMsgBody.equals(message)){
                                    textView.setText("Equal");
                                    startService(new Intent(VerifyActivity.this, RegisterService.class));

                                }
                                abortBroadcast();
                            }else{
                            }

                        }

                    }
                }

            }

        };
        registerReceiver(receiver, filter);




        intent=getIntent();
        if (intent.getBooleanExtra("sms",false)){
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(mob, null, message, null, null);
          //  sms.sendTextMessage();
        }

        btverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pin.equals(etpin.getText().toString())){
                    startService(new Intent(VerifyActivity.this, RegisterService.class));
                }else{
                    textView.setText("Pin wrongss");

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verify, menu);
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
