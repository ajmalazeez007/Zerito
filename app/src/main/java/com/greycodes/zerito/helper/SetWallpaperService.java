package com.greycodes.zerito.helper;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;

import com.greycodes.zerito.util.Utils;

import java.io.InputStream;


public class SetWallpaperService extends Service {
   String imageURL;
    public SetWallpaperService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        imageURL=intent.getStringExtra("url");
        new DownloadImage().execute();
        return super.onStartCommand(intent, flags, startId);
    }
    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {



        @Override
        protected Bitmap doInBackground(String... URL) {


            Bitmap bitmap = null;
            try {
// Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
// Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
// Set the bitmap into ImageView
          //  image.setImageBitmap(result);
            Utils utils = new Utils(getApplicationContext());
            utils.setAsWallpaper(result);
// Close progressdialog
        }
    }
}
