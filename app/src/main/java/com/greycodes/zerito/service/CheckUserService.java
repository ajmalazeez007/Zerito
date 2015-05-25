package com.greycodes.zerito.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.greycodes.zerito.HomeActivity;
import com.greycodes.zerito.VerifyActivity;
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

public class CheckUserService extends Service {
    String mobile,url,results;
    int flag;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mobile=intent.getStringExtra("mobile");
        mobile = mobile.replace("-", "");
        mobile = mobile.replace(" ", "");
        url="http://ieeelinktest.x20.in/app2/checkuser.php";
        new CheckUserAsync().execute();
        return super.onStartCommand(intent, flags, startId);
    }

    class CheckUserAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
// TODO Auto-generated method stub
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mob_num", mobile));


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
                JSONObject jsonObject=new JSONObject(results);
                if (jsonObject.getBoolean("success")){

                        String name=jsonObject.getString("name");
                        AppController.afpName=name;
                        AppController.afpNumber=mobile;
                        AppController.afptype=true;
                        HomeActivity.setpopup();
                        HomeActivity.progressDialog.dismiss();


                }else{

                        String name="User doesnt Exist";
                        AppController.afpName=name;
                        AppController.afpNumber=mobile;
                        AppController.afptype=false;
                        HomeActivity.setpopup();
                        HomeActivity.progressDialog.dismiss();


                }
            } catch (Exception e) {
                e.printStackTrace();

                HomeActivity.progressDialog.dismiss();



                Toast.makeText(getApplicationContext(),"Coudn't connect to the Internet",Toast.LENGTH_LONG).show();
            }
            stopSelf();

        }
    }
}
