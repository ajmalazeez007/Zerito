package com.greycodes.zerito;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.greycodes.zerito.helper.RegisterService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class RegisterActivity extends ActionBarActivity {
    public static final String PROPERTY_REG_ID = "registration_id";
    static final String SERVER_URL = "http://ieeelinktest.x20.in/app2/register.php";
    //static final String SERVER_URL = "http://ieeelinktest.x20.in/register.php";
    public static String acc = "";
    public String msg = "";
    Boolean stat=true;
    public static String accn = "";
    String SENDER_ID = "187588160331";

    static final String TAG = "GCMDemo";
    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
    String phone="";
    String name="";
    String pin="";
    EditText etphone;
    EditText etname,etpin;
    ImageView submit;
    TextView tvsignin;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //	setContentView(R.layout.activity_main);
        setContentView(R.layout.register_activity);
        sharedPreferences= getSharedPreferences("zerito", Context.MODE_PRIVATE);

        try {
            Account[] accounts = AccountManager.get(this).getAccounts();

            acc= accounts[0].name;
            Log.d("Account", "Name " + etname);
            accn=acc.substring(0, acc.indexOf('@'));
        } catch (Exception e) {
            e.printStackTrace();
        }

        etname =(EditText)findViewById(R.id.name);
        etphone =(EditText)findViewById(R.id.phone);
        etpin =(EditText)findViewById(R.id.pin);
        tvsignin = (TextView) findViewById(R.id.signin);
        submit = (ImageView) findViewById(R.id.submit);

        context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);

        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,SignInActivity.class));
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                name = etname.getText().toString();

                pin=etpin.getText().toString();
                phone = etphone.getText().toString();

                // Start func if NET

                //setContentView(R.layout.activity_main);

                //context = getApplicationContext();
                //gcm = GoogleCloudMessaging.getInstance(this);
                //regid = getRegistrationId(context);
                if(pin.length()!=4){
                    Toast.makeText(getApplicationContext(),"You need to enter 4 digit pin",Toast.LENGTH_LONG).show();

                }else if (name.length()<3){
                    Toast.makeText(getApplicationContext(),"name must be atleast 3 letter",Toast.LENGTH_LONG).show();

                }else if(phone.length()<5){
                    Toast.makeText(getApplicationContext(),"phone number must be atleast 5 digit",Toast.LENGTH_LONG).show();

                }else if (!sharedPreferences.getBoolean("register",false))  {
                    registerInBackground();
                } else {
                    Toast.makeText(getApplicationContext(),"You are already registered",Toast.LENGTH_LONG).show();

       /* PackageManager p = getPackageManager();
        p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);*/
              }


            }
        });

        //Intent intent=new Intent(this, GcmBroadcastReceiver.class);
        //startActivity(intent);

        //g.onReceive(this,);

    }
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        Toast.makeText(getApplicationContext(),registrationId,Toast.LENGTH_LONG).show();
        if (registrationId.equals("")) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; setContentView(R.layout.activity_main);if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        return registrationId;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    private void registerInBackground() {
        Toast.makeText(getApplicationContext(),"Reg in bag",Toast.LENGTH_LONG).show();

        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();
                   // storeRegistrationId(context, regid);
                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    stat=false;
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            private void sendRegistrationIdToBackend() {
                final int MAX_ATTEMPTS = 5;
                final int BACKOFF_MILLI_SECONDS = 2000;
                final Random random = new Random();
                Log.i(TAG, "registering device (regId = " + regid + ")");

                Intent intent = new Intent(RegisterActivity.this, RegisterService.class);
                intent.putExtra("name",name);
                intent.putExtra("pin",pin);
                intent.putExtra("id",regid);
                intent.putExtra("mob",phone);

                startService(intent);
          /*      long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                // Once GCM returns a registration id, we need to register on our server
                // As the server might be down, we will retry it a couple
                // times.
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    Log.d(TAG, "Attempt #" + i + " to register");
                    try {
                     //   post(serverUrl, params);
                        // displayMessage(context, "Registered");
                        return;
                    } catch (Exception e) {
                        // Here we are simplifying and retrying on any error; in a real
                        // application, it should retry only on unrecoverable errors
                        // (like HTTP error code 503).
                        Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                        if (i == MAX_ATTEMPTS) {
                            break;
                        }
                        try {
                            Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            // Activity finished before we complete - exit.
                            Log.d(TAG, "Thread interrupted: abort remaining retries!");
                            Thread.currentThread().interrupt();
                            return;
                        }
                        // increase backoff exponentially
                        backoff *= 2;
                    }
                }
                //  String message = context.getString(R.string.server_register_error,
                //        MAX_ATTEMPTS);
                //CommonUtilities.displayMessage(context, message);
*/
            }
    /*        private  void post(String endpoint, Map<String, String> params)throws IOException{
                URL url;
                try {
                    url = new URL(endpoint);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("invalid url: " + endpoint);
                }
                StringBuilder bodyBuilder = new StringBuilder();
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                // constructs the POST body using the parameters
                while (iterator.hasNext()) {
                    Map.Entry<String, String> param = iterator.next();
                    bodyBuilder.append(param.getKey()).append('=')
                            .append(param.getValue());
                    if (iterator.hasNext()) {
                        bodyBuilder.append('&');
                    }
                }
                String body = bodyBuilder.toString();
                Log.v(TAG, "Posting '" + body+ "' to " + url);
                byte[] bytes = body.getBytes();
                HttpURLConnection conn = null;
                try {
                    Log.e("URL", "> " + url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setFixedLengthStreamingMode(bytes.length);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded;charset=UTF-8");
                    // post the request
                    OutputStream out = conn.getOutputStream();
                    out.write(bytes);
                    out.close();
                    // handle the response



                    int status = conn.getResponseCode();
                    if (status != 200) {
                        throw new IOException("Post failed with error code " + status);
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
*/
            protected void onPostExecute(String msg) {
                //setContentView(R.layout.activity_gcm_broadcast_receiver);
              //  Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                /*
                if(stat){

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("mobnum",phone);
                    editor.putString("name",name);
                    editor.putString("pin",pin);
                    editor.putBoolean("register", true);
                    editor.commit();
                    Intent intent =new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_LONG).show();


                }
*/
            }


        }.execute(null, null, null);}

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        //int appVersion = getAppVersion(context);
        // Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        //  editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}