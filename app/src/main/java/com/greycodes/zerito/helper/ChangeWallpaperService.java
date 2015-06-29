package com.greycodes.zerito.helper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ChangeWallpaperService extends Service {
   String url,imgurl,mob1,mob2,results,imgtext;
    HttpResponse response;
    SharedPreferences sharedPreferences;
    String ts,tzone;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            url="http://ieeelinktest.x20.in/app2/wall_change.php";
            sharedPreferences= getSharedPreferences("zerito", Context.MODE_PRIVATE);
            mob1=sharedPreferences.getString("mobnum","000000");
            imgurl= intent.getStringExtra("url");
            imgtext= intent.getStringExtra("imgtext");
            mob2= AppController.selectedmob;
            Long tsLong = System.currentTimeMillis()/1000;

            ts = tsLong.toString();
            Log.d("timestamp", ts);
            TimeZone tz = TimeZone.getDefault();
            tzone=tz.getID();

           new ChangeWallpaperAsync().execute();
        } catch (Exception e) {
            stopSelf();
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    class ChangeWallpaperAsync extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(),"Wallpaper Changed",Toast.LENGTH_LONG).show();

            super.onPostExecute(aVoid);

            stopSelf();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
// Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mob1", mob1));
                nameValuePairs.add(new BasicNameValuePair("mob2", mob2));
                nameValuePairs.add(new BasicNameValuePair("img_link",imgurl));
                nameValuePairs.add(new BasicNameValuePair("img_text",imgtext));
                nameValuePairs.add(new BasicNameValuePair("timestamp",ts));
                nameValuePairs.add(new BasicNameValuePair("timezone",tzone));
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
            }catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;
        }
    }
}
