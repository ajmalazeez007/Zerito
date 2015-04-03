package com.greycodes.zerito.helper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.greycodes.zerito.FriendRequestActivity;
import com.greycodes.zerito.app.AppController;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FriendAcceptService extends Service {
    SharedPreferences sharedPreferences;
    String results,url,mob1,mob2,flag;
    int type;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            sharedPreferences= getSharedPreferences("zerito",MODE_PRIVATE);

            mob2=sharedPreferences.getString("mobnum","");
            mob1=intent.getStringExtra("mob2");
            flag=intent.getStringExtra("flag");
            type = intent.getIntExtra("type",0);
            url="http://ieeelinktest.x20.in/app2/pair_complete.php";
            Toast.makeText(getApplicationContext(),""+mob1,Toast.LENGTH_LONG).show();
            new FriendAcceptAsync().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class FriendAcceptAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
// TODO Auto-generated method stub
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mob1", mob1));
                nameValuePairs.add(new BasicNameValuePair("mob2", mob2));
                nameValuePairs.add(new BasicNameValuePair("type", ""+type));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                InputStream inputstream = null;
                HttpEntity entity = response.getEntity();

                inputstream = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"UTF-8"),8);
                StringBuilder theStringBuilder = new StringBuilder();
                String line = null;
                while((line= reader.readLine())!=null){
                    theStringBuilder.append(line+ '\n');
                }
                results = theStringBuilder.toString();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
// TODO Auto-generated method stub
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(results);
              if(jsonObject.getBoolean("success")){
                  Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();

              }else if (!jsonObject.getBoolean("success")){
                  Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();

              }else{

              }
                stopSelf();


            } catch (JSONException e) {
                e.printStackTrace();
                new FriendAcceptAsync().execute();
            }


        }
    }
}
