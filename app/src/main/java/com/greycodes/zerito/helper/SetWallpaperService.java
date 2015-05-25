package com.greycodes.zerito.helper;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import com.greycodes.zerito.R;
import com.greycodes.zerito.SplashActivity;
import com.greycodes.zerito.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class SetWallpaperService extends Service {
    String imageURL, imgText;
    private NotificationManager mNotificationManager;
    public static int NOTIFICATION_ID = 1;
    private DownloadManager downloadManager;
    private long downloadReference;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            imageURL = intent.getStringExtra("url");
            imgText = intent.getStringExtra("imgtext");
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(imageURL);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(true);
            request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, "wallpaper");
            downloadReference = downloadManager.enqueue(request);
        } catch (Exception e) {
            stopSelf();
            e.printStackTrace();
        }
        // new DownloadImage().execute();
        try {
            DownLoadComplte mDownload = new DownLoadComplte();
            registerReceiver(mDownload, new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }



    void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), SplashActivity.class), 0);
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
        NOTIFICATION_ID = rand.nextInt((1000 - 10) + 1) + 10;
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID++;
    }

    private class DownLoadComplte extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equalsIgnoreCase(
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        long downloadId = intent.getLongExtra(
                                DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downloadReference);
                        Cursor c = downloadManager.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt(columnIndex)) {

                                String uriString = c
                                        .getString(c
                                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                Uri uri = Uri.parse(uriString);
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                                    Utils utils = new Utils(getApplicationContext());
                                    utils.setAsWallpaper(bitmap, imgText);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }





                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        }
    }



