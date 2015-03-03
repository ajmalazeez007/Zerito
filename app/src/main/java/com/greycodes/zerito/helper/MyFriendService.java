package com.greycodes.zerito.helper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.greycodes.zerito.HomeActivity;
import com.greycodes.zerito.app.AppController;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MyFriendService extends Service {
    SharedPreferences sharedPreferences;
    String results,url;
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
        sharedPreferences= getSharedPreferences("zerito",MODE_PRIVATE);
        String id=sharedPreferences.getString("id","");
        url="http://www.google.com";
        new MyFriendsAsync().execute();
        return super.onStartCommand(intent, flags, startId);
    }


    class MyFriendsAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Please wait...loading data...",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
// TODO Auto-generated method stub
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Content-type","application/json");
            InputStream inputstream = null;
            try{
                org.apache.http.HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                inputstream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"UTF-8"),8);
                StringBuilder theStringBuilder = new StringBuilder();
                String line = null;
                while((line= reader.readLine())!=null){
                    theStringBuilder.append(line+ '\n');
                }
                results = theStringBuilder.toString();
            }catch(Exception e){
                stopSelf();
            }finally{
                try{
                    if(inputstream!=null)
                        inputstream.close();
                }catch(Exception e){
                    stopSelf();
                }
            }
            return results;
        }
        @Override
        protected void onPostExecute(String result) {
// TODO Auto-generated method stub
            super.onPostExecute(result);
            results= "{\n" +
                    "\t\"friends\":[{\n" +
                    "\t\t\"id\":1,\n" +
                    "\t\t\"name\":\"Ajmal\",\n" +
                    "\t\t\"mob\":\"9020404022\"\n" +
                    "\t\t},{\n" +
                    "\t\t\"id\":2,\n" +
                    "\t\t\"name\":\"Aswin\",\n" +
                    "\t\t\"mob\":\"8891234567\"\n" +
                    "\t\t},{\n" +
                    "\t\t\"id\":3,\n" +
                    "\t\t\"name\":\"Kishan\",\n" +
                    "\t\t\"mob\":\"666661111\"\n" +
                    "\t\t}]\n" +
                    "}";
            try {
                JSONObject jsonObject = new JSONObject(results);
                JSONArray jsonArray = jsonObject.getJSONArray("friends");
                count = jsonArray.length();
                name = new String[count];
                mobnum = new String[count];
                id = new int[count];
                for (int i=0;i<count;i++){
                    name[i]= jsonArray.getJSONObject(i).getString("name");
                    mobnum[i]= jsonArray.getJSONObject(i).getString("mob");
                    id[i]= jsonArray.getJSONObject(i).getInt("id");
                }
                AppController.name = name;
                AppController.mobnum = mobnum;
                AppController.id = id;
                MyFriendsAdapter myFriendService= new MyFriendsAdapter(getApplicationContext(),id,name,mobnum);
                AppController.myFriendsAdapter=myFriendService;
                Intent intent=new Intent(MyFriendService.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Error: "+ e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
                stopSelf();
            }


        }
    }
}
