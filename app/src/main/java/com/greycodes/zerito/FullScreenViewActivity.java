package com.greycodes.zerito;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greycodes.zerito.app.AppController;
import com.greycodes.zerito.helper.ChangeWallpaperService;
import com.greycodes.zerito.picasa.model.Wallpaper;
import com.greycodes.zerito.util.Utils;


public class FullScreenViewActivity extends ActionBarActivity implements OnClickListener {
	private static final String TAG = FullScreenViewActivity.class
			.getSimpleName();
	public static final String TAG_SEL_IMAGE = "selectedImage";
	private Wallpaper selectedPhoto;
	private ImageView fullImageView;
	private LinearLayout myWallpaper, friendsWallpaper;
	private Utils utils;
	private ProgressBar pbLoader;
    String fullResolutionUrl;
    EditText et_custom;
    TextView tv_preview;
    // Picasa JSON response node keys
	private static final String TAG_ENTRY = "entry",
			TAG_MEDIA_GROUP = "media$group",
			TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
			TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_image);
        getSupportActionBar().setTitle(AppController.selectedname);
        tv_preview = (TextView) findViewById(R.id.fi_preview);
        et_custom= (EditText) findViewById(R.id.fi_customtext);
		fullImageView = (ImageView) findViewById(R.id.imgFullscreen);
		myWallpaper = (LinearLayout) findViewById(R.id.fi_mywallpaper);
		friendsWallpaper = (LinearLayout) findViewById(R.id.friendswallpaper);
		pbLoader = (ProgressBar) findViewById(R.id.pbLoader);

		// hide the action bar in fullscreen mode

    /*    if(!AppController.custommsg){
            et_custom.setVisibility(View.GONE);
            tv_preview.setVisibility(View.GONE);
        }*/

		utils = new Utils(getApplicationContext());

		// layout click listeners
		myWallpaper.setOnClickListener(this);
		friendsWallpaper.setOnClickListener(this);
        tv_preview.setOnClickListener(this);
        /*Only for custom album */
       // tv_preview.setVisibility(View.GONE);
      //  et_custom.setVisibility(View.GONE);

		// setting layout buttons alpha/opacity
		myWallpaper.getBackground().setAlpha(70);
		friendsWallpaper.getBackground().setAlpha(70);

		Intent i = getIntent();
		selectedPhoto = (Wallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);

		// check for selected photo null
		if (selectedPhoto != null) {

			// fetch photo full resolution image by making another json request
			fetchFullResolutionImage();

		} else {
			Toast.makeText(getApplicationContext(),
					getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Fetching image fullresolution json
	 * */
	private void fetchFullResolutionImage() {
		String url = selectedPhoto.getPhotoJson();

		// show loader before making request
		pbLoader.setVisibility(View.VISIBLE);
		myWallpaper.setVisibility(View.GONE);
		friendsWallpaper.setVisibility(View.GONE);

		// volley's json obj request
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG,
								"Image full resolution json: "
										+ response.toString());
						try {
							// Parsing the json response
							JSONObject entry = response
									.getJSONObject(TAG_ENTRY);

							JSONArray mediacontentArry = entry.getJSONObject(
									TAG_MEDIA_GROUP).getJSONArray(
									TAG_MEDIA_CONTENT);

							JSONObject mediaObj = (JSONObject) mediacontentArry
									.get(0);

							 fullResolutionUrl = mediaObj
									.getString(TAG_IMG_URL);

							// image full resolution widht and height
							final int width = mediaObj.getInt(TAG_IMG_WIDTH);
							final int height = mediaObj.getInt(TAG_IMG_HEIGHT);

							Log.d(TAG, "Full resolution image. url: "
									+ fullResolutionUrl + ", w: " + width
									+ ", h: " + height);
							ImageLoader imageLoader = AppController
									.getInstance().getImageLoader();

							// We download image into ImageView instead of
							// NetworkImageView to have callback methods
							// Currently NetworkImageView doesn't have callback
							// methods

							imageLoader.get(fullResolutionUrl,
									new ImageListener() {

										@Override
										public void onErrorResponse(
												VolleyError arg0) {
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.msg_wall_fetch_error),
													Toast.LENGTH_LONG).show();
										}

										@Override
										public void onResponse(
												ImageContainer response,
												boolean arg1) {
											if (response.getBitmap() != null) {
												// load bitmap into imageview
												fullImageView
														.setImageBitmap(response
																.getBitmap());
												adjustImageAspect(width, height);

												// hide loader and show set &
												// download buttons
												pbLoader.setVisibility(View.GONE);
												myWallpaper
														.setVisibility(View.VISIBLE);
												friendsWallpaper
														.setVisibility(View.VISIBLE);
											}
										}
									});

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(),
									getString(R.string.msg_unknown_error),
									Toast.LENGTH_LONG).show();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Error: " + error.getMessage());
						// unable to fetch wallpapers
						// either google username is wrong or
						// devices doesn't have internet connection
						Toast.makeText(getApplicationContext(),
								getString(R.string.msg_wall_fetch_error),
								Toast.LENGTH_LONG).show();

					}
				});

		// Remove the url from cache
		AppController.getInstance().getRequestQueue().getCache().remove(url);

		// Disable the cache for this url, so that it always fetches updated
		// json
		jsonObjReq.setShouldCache(false);

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq);
	}

	/**
	 * Adjusting the image aspect ration to scroll horizontally, Image height
	 * will be screen height, width will be calculated respected to height
	 * */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void adjustImageAspect(int bWidth, int bHeight) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		if (bWidth == 0 || bHeight == 0)
			return;

		int sHeight = 0;

		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			sHeight = size.y;
		} else {
			Display display = getWindowManager().getDefaultDisplay();
			sHeight = display.getHeight();
		}

		int new_width = (int) Math.floor((double) bWidth * (double) sHeight
				/ (double) bHeight);
		params.width = new_width;
		params.height = sHeight;

		Log.d(TAG, "Fullscreen image new dimensions: w = " + new_width
				+ ", h = " + sHeight);

		fullImageView.setLayoutParams(params);
	}

	/**
	 * View click listener
	 * */
	@Override
	public void onClick(View v) {
		Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
				.getBitmap();
		switch (v.getId()) {
		// button Download Wallpaper tapped
		case R.id.friendswallpaper:
			//utils.saveImageToSDCard(bitmap);
            AppController.imageBitmap=bitmap;
            String imgtext= et_custom.getText().toString();
            Intent intent = new Intent(FullScreenViewActivity.this, ChangeWallpaperService.class);
            intent.putExtra("url",fullResolutionUrl);
            intent.putExtra("imgtext",imgtext);
            startService(intent);
           // startActivity(new Intent(FullScreenViewActivity.this,PreviewActivity.class));
			break;
		// button Set As Wallpaper tapped
		case R.id.fi_mywallpaper:
			utils.setAsWallpaper(bitmap,"");
			break;
            case R.id.fi_preview:
                AppController.imageBitmap=bitmap;
                String msg=et_custom.getText().toString();
                intent=new Intent(FullScreenViewActivity.this,PreviewActivity.class);
                intent.putExtra("msg",msg);
            startActivity(intent);
                break;
		default:
			break;
		}

	}
}