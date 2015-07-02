package com.greycodes.zerito.helper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.greycodes.zerito.HistoryActivity;
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


public class HistoryService extends Service {
    SharedPreferences sharedPreferences;
    String results,url,mob1;
    String[] name, time,link,text;
    int[] type;
    int count;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            sharedPreferences= getSharedPreferences("zerito",MODE_PRIVATE);
            mob1=sharedPreferences.getString("mobnum","");
            url="http://ieeelinktest.x20.in/app2/history.php";
           // Toast.makeText(getApplicationContext(),""+mob1,Toast.LENGTH_LONG).show();
            new FriendRequestAsync().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    class FriendRequestAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
// TODO Auto-generated method stub
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mob_num", mob1));

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
                JSONArray jsonArray = jsonObject.getJSONArray("history");
                count = jsonArray.length();
                name = new String[count];
                time = new String[count];
                link = new String[count];
                text = new String[count];
                type = new int[count];
                for (int i=0;i<count;i++){
                    name[i]= jsonArray.getJSONObject(i).getString("name");
                    time[i]= jsonArray.getJSONObject(i).getString("time");
                    link[i]= jsonArray.getJSONObject(i).getString("link");
                    text[i]= jsonArray.getJSONObject(i).getString("text");
                    type[i]= jsonArray.getJSONObject(i).getInt("type");
                }

                HistoryAdapter historyAdapter= new HistoryAdapter(getApplicationContext(), name,time, type,link,text);
                AppController.historyAdapter=historyAdapter;
                Intent intent=new Intent(HistoryService.this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Coudn't connect to the Internet", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                stopSelf();
            }


        }
    }
}
