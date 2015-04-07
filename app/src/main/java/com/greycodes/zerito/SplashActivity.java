package com.greycodes.zerito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greycodes.zerito.app.AppConst;
import com.greycodes.zerito.app.AppController;
import com.greycodes.zerito.helper.FriendRequestService;
import com.greycodes.zerito.helper.MyFriendService;
import com.greycodes.zerito.picasa.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class SplashActivity extends Activity {
	private static final String TAG = SplashActivity.class.getSimpleName();
	private static final String TAG_FEED = "feed", TAG_ENTRY = "entry",
			TAG_GPHOTO_ID = "gphoto$id", TAG_T = "$t",
			TAG_ALBUM_TITLE = "title";
SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		// Preparing volley's json object request
        int type=getIntent().getIntExtra("type",0);
        if(type==0){
            setContentView(R.layout.activity_splash);
            sharedPreferences= getSharedPreferences("zerito", Context.MODE_PRIVATE);
            // Picasa request to get list of albums
            String url = AppConst.URL_PICASA_ALBUMS
                    .replace("_PICASA_USER_", AppController.getInstance()
                            .getPrefManger().getGoogleUserName());

            Log.d(TAG, "Albums request url: " + url);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Albums Response: " + response.toString());
                    List<Category> albums = new ArrayList<Category>();
                    try {
                        // Parsing the json response
                        JSONArray entry = response.getJSONObject(TAG_FEED)
                                .getJSONArray(TAG_ENTRY);

                        // loop through albums nodes and add them to album
                        // list
                        for (int i = 0; i < entry.length(); i++) {
                            JSONObject albumObj = (JSONObject) entry.get(i);
                            // album id
                            String albumId = albumObj.getJSONObject(
                                    TAG_GPHOTO_ID).getString(TAG_T);

                            // album title
                            String albumTitle = albumObj.getJSONObject(
                                    TAG_ALBUM_TITLE).getString(TAG_T);

                            Category album = new Category();
                            album.setId(albumId);
                            album.setTitle(albumTitle);

                            // add album to list
                            albums.add(album);

                            Log.d(TAG, "Album Id: " + albumId
                                    + ", Album Title: " + albumTitle);
                        }

                        // Store albums in shared pref
                        AppController.getInstance().getPrefManger()
                                .storeCategories(albums);

                        // String the main activity

                        Intent intent;
                        if (sharedPreferences.getBoolean("register",false)) {
                            if (sharedPreferences.getString("name","").equals("")){
                                intent = new Intent(SplashActivity.this,UserNameActivity.class);
                                startActivity(intent);
                                finish();


                            }else
                            if (sharedPreferences.getBoolean("smsverification",false)){
                                intent = new Intent(SplashActivity.this,
                                        MyFriendService.class);
                                startService(intent);
                            }else {
                                intent = new Intent(SplashActivity.this,
                                        VerifyActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                          intent = new Intent(getApplicationContext(),
                                    RegisterActivity.class);
                            startActivity(intent);

                            finish();
                        }
                      ;
                        // closing spalsh activity

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Volley Error: " + error.getMessage());

                    // show error toast
                    Toast.makeText(getApplicationContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();

                    // Unable to fetch albums
                    // check for existing Albums data in Shared Preferences
                    if (AppController.getInstance().getPrefManger()
                            .getCategories() != null && AppController.getInstance().getPrefManger()
                            .getCategories().size() > 0) {
                        // String the main activity
                        Intent intent;
                        if (sharedPreferences.getBoolean("register",false)) {
                            if (sharedPreferences.getString("name","").equals("")){
                                intent = new Intent(SplashActivity.this,UserNameActivity.class);
                                startActivity(intent);
                                finish();

                            }else
                            if (sharedPreferences.getBoolean("smsverification",false)){
                                intent = new Intent(SplashActivity.this,
                                        MyFriendService.class);
                                startService(intent);
                            }else {
                                intent = new Intent(SplashActivity.this,
                                        VerifyActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            intent = new Intent(SplashActivity.this,
                                    RegisterActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        // closing spalsh activity
                        finish();
                    } else {
                        // Albums data not present in the shared preferences
                        // Launch settings activity, so that user can modify
                        // the settings

                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                    }

                }
            });

            // disable the cache for this request, so that it always fetches updated
            // json
            jsonObjReq.setShouldCache(false);

            // Making the request
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }else {
            setContentView(R.layout.loading_layout);
            Intent intent = new Intent(SplashActivity.this, FriendRequestService.class);
            startActivity(intent);
            finish();
        }


	}

}
