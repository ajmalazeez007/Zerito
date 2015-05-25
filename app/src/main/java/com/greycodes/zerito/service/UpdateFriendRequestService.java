package com.greycodes.zerito.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.greycodes.zerito.FriendRequestActivity;
import com.greycodes.zerito.app.AppController;
import com.greycodes.zerito.helper.FriendRequestAdapter;

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

public class UpdateFriendRequestService extends Service {
    SharedPreferences sharedPreferences;
    String results,url,mob1;
    String[] name,mobnum;
    int[] id;
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
            url="http://ieeelinktest.x20.in/app2/pendingrequests.php";
           // Toast.makeText(getApplicationContext(), "" + mob1, Toast.LENGTH_LONG).show();
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
                nameValuePairs.add(new BasicNameValuePair("mob1", mob1));

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
          //  Toast.makeText(getApplicationContext(),results,Toast.LENGTH_LONG).show();

            try {
                JSONObject jsonObject = new JSONObject(results);
                JSONArray jsonArray = jsonObject.getJSONArray("friends");

                count = jsonArray.length();
                if (count==0){
                    Toast.makeText(getApplicationContext(),"No friends",Toast.LENGTH_LONG).show();

                }
                name = new String[count];
                mobnum = new String[count];
                id = new int[count];
                for (int i=0;i<count;i++){
                    name[i]= jsonArray.getJSONObject(i).getString("name");
                    mobnum[i]= jsonArray.getJSONObject(i).getString("mob_no");
                    id[i]= jsonArray.getJSONObject(i).getInt("id");
                }
                AppController.name = name;
                AppController.mobnum = mobnum;
                AppController.id = id;
                FriendRequestAdapter friendRequestAdapter= new FriendRequestAdapter(getApplicationContext(),name,mobnum, id);
                AppController.friendRequestAdapter=friendRequestAdapter;
                FriendRequestActivity.updateListView();
                stopSelf();


            } catch (JSONException e) {
                e.printStackTrace();
                new FriendRequestAsync().execute();
            }


        }
    }
}
