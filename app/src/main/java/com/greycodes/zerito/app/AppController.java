package com.greycodes.zerito.app;



import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.greycodes.zerito.helper.FriendRequestAdapter;
import com.greycodes.zerito.helper.HistoryAdapter;
import com.greycodes.zerito.helper.MyFriendsAdapter;
import com.greycodes.zerito.util.LruBitmapCache;
import com.greycodes.zerito.util.PrefManager;

public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	LruBitmapCache mLruBitmapCache;

	private static AppController mInstance;
	private PrefManager pref;
    public static FriendRequestAdapter friendRequestAdapter;
    public static String[] name,mobnum;
    public static int[] id;
    public static MyFriendsAdapter myFriendsAdapter;
    public static String selectedmob,selectedname;
    public static boolean custommsg;
    public static Bitmap imageBitmap;
    public static HistoryAdapter historyAdapter;
    public static String afpName,afpNumber;
    public static boolean afptype;
    public static Uri customImage;
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		pref = new PrefManager(this);
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public PrefManager getPrefManger() {
		if (pref == null) {
			pref = new PrefManager(this);
		}

		return pref;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			getLruBitmapCache();
			mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
		}

		return this.mImageLoader;
	}

	public LruBitmapCache getLruBitmapCache() {
		if (mLruBitmapCache == null)
			mLruBitmapCache = new LruBitmapCache();
		return this.mLruBitmapCache;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}
