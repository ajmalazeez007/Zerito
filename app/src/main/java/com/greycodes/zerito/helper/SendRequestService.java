package com.greycodes.zerito.helper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SendRequestService extends Service {
    String mob1,mob2,url,results;
    HttpResponse response;
    SharedPreferences sharedPreferences;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            sharedPreferences= getSharedPreferences("zerito", Context.MODE_PRIVATE);
            url = "http://ieeelinktest.x20.in/app2/pair.php";
            mob1=sharedPreferences.getString("mobnum", "000000");
            mob2= AppController.afpNumber;
            new SendRequestAsync().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    class SendRequestAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mob1", mob1));
                nameValuePairs.add(new BasicNameValuePair("mob2", mob2));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                 response = httpclient.execute(httppost);
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONObject jsonObject = new JSONObject(results);
                if(jsonObject.getInt("success")==1){
                   Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                }else if (jsonObject.getInt("success")==2){
                    Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();

                }
                stopSelf();
            } catch (Exception e) {
                e.printStackTrace();

                if (!mob2.equals("")){
                    SystemClock.sleep(2000);
                    new SendRequestAsync().execute();
                }else{
                    stopSelf();
                }

            }

        }
    }
    }

