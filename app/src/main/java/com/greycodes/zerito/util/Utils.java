package com.greycodes.zerito.util;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.greycodes.zerito.R;
import com.greycodes.zerito.app.AppController;

public class Utils {
	private String TAG = Utils.class.getSimpleName();
	private Context _context;
	private PrefManager pref;

	// constructor
	public Utils(Context context) {
		this._context = context;
		pref = new PrefManager(_context);
	}

	/*
	 * getting screen width
	 */
	@SuppressWarnings("deprecation")
	public int getScreenWidth() {
		int columnWidth;
		WindowManager wm = (WindowManager) _context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) {
			// Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		columnWidth = point.x;
		return columnWidth;
	}

	public void saveImageToSDCard(Bitmap bitmap) {
		File myDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				pref.getGalleryName());

		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "Wallpaper-" + n + ".jpg";
		File file = new File(myDir, fname);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Toast.makeText(
					_context,
					_context.getString(R.string.toast_saved).replace("#",
							"\"" + pref.getGalleryName() + "\""),
					Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Wallpaper saved to: " + file.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(_context,
					_context.getString(R.string.toast_saved_failed),
					Toast.LENGTH_SHORT).show();
		}
	}

	public void setAsWallpaper(Bitmap background,String text) {
		try {

            Resources res = _context.getResources();
             background = background.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(background);
            Typeface font = null;
            try {
                font = Typeface.createFromAsset(res.getAssets(), "fonts/asd.ttf");
                font = Typeface.create(font, Typeface.BOLD);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Paint paint = new Paint();
            try {
                paint.setTypeface(font);
            } catch (Exception e) {
                e.printStackTrace();
            }
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(2.0f, 1.0f, 1.0f, Color.BLACK);
            float fontSize = 50;
            //getFontSize(background.getWidth(), "HELLO TESTING", paint); //You'll have to define a way to find a size that fits, or just use a constant size.

            paint.setTextSize(fontSize);
            canvas.drawText(text, (background.getWidth() - paint.measureText(text)) / 2,
                    background.getHeight()/2, paint); //You might want to do something different. In my case every image has a filler in the bottom which is 50px.

            WallpaperManager wm = WallpaperManager.getInstance(_context);

			wm.setBitmap(background);
			Toast.makeText(_context,_context.getString(R.string.toast_wallpaper_set),
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(_context,
					_context.getString(R.string.toast_wallpaper_set_failed)+e.toString(),
					Toast.LENGTH_SHORT).show();
		}
	}
}