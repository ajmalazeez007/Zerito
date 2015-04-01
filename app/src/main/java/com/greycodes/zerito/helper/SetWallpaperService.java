package com.greycodes.zerito.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.greycodes.zerito.MainActivity;
import com.greycodes.zerito.R;
import com.greycodes.zerito.SplashActivity;
import com.greycodes.zerito.util.Utils;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Random;
import java.util.TimeZone;


public class SetWallpaperService extends Service {
   String imageURL,imgText;
    private NotificationManager mNotificationManager;
    public static int NOTIFICATION_ID = 1;
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
        imgText=intent.getStringExtra("imgtext");
        Toast.makeText(getApplicationContext(),"Wallchange service",Toast.LENGTH_LONG).show();
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
            try {
                Utils utils = new Utils(getApplicationContext());
                utils.setAsWallpaper(result,imgText);
                sendNotification("Wallpaper change service");
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopSelf();
// Close progressdialog
        }
    }

    void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,      new Intent(getApplicationContext(), SplashActivity.class), 0);
      //  contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 0);


        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Zerito")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setDefaults(defaults);
        mBuilder.setContentIntent(contentIntent);
        Random rand = new Random();
        NOTIFICATION_ID= rand.nextInt((1000 - 10) + 1) + 10;
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID++;
    }
}
