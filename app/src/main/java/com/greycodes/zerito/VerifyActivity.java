package com.greycodes.zerito;

import android.app.Activity;
import android.app.PendingIntent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.greycodes.zerito.helper.RegisterService;


public class VerifyActivity extends ActionBarActivity {
    Intent intent;
    SharedPreferences sharedPreferences;
    String mob,message,pin;
    IntentFilter filter;
    private BroadcastReceiver receiver;
    static final String ACTION ="android.provider.Telephony.SMS_RECEIVED";

    EditText etpin;
    ImageView btverify;
    static EditText username;
    static Button submit;
    static Context context;
    PendingIntent sentPI;
    String SENT = "SMS_SENT";
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usernamelayout);

        sharedPreferences= getSharedPreferences("zerito", Context.MODE_PRIVATE);

            accountVerification();



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
    void accountVerification(){
        setContentView(R.layout.activity_verify);

        etpin= (EditText) findViewById(R.id.ul_username);
        btverify= (ImageView) findViewById(R.id.ul_submit);

        mob=sharedPreferences.getString("mobnum", "");
        pin=sharedPreferences.getString("pin", "");

        sentPI= PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        filter  = new IntentFilter();
        filter.addAction(ACTION);
        filter.setPriority(999);
        message="Welcome to Zeito.Your confirmation number is  "+pin;

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure.Message not sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service .Message not sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU .Message not sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off .Message not sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
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
                                    Toast.makeText(getBaseContext(), "Succesfully Verified",
                                            Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(),mob,Toast.LENGTH_LONG).show();
            try {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(mob, null, message, sentPI, null);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Couldn't send sms.Unknown error occured!",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            //  sms.sendTextMessage();
        }

        btverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pin.equals(etpin.getText().toString())){
                    startService(new Intent(VerifyActivity.this, RegisterService.class));
                }else{
                    Toast.makeText(getBaseContext(), "Wrong Pin",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
