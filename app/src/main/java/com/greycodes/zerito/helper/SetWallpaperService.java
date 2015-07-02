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
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.greycodes.zerito.GcmActivity;
import com.greycodes.zerito.R;
import com.greycodes.zerito.SplashActivity;
import com.greycodes.zerito.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class SetWallpaperService extends Service {
    String imageURL, imgText,message;
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
            message = intent.getStringExtra("message");
        } catch (Exception e) {

            message="";
            e.printStackTrace();
        }
        downloadManager  = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        Uri resource = Uri.parse(imageURL);
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setTitle("Zerito");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, "wallpaper");
        downloadReference = downloadManager.enqueue(request);
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


//        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),"DOWnload complete",Toast.LENGTH_LONG).show();
           // queryDownloadStatus();
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            setWall(downloadId);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    void setWall(long downloadId){
     //   long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
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
                    if (message.length()>2) {
                        startNotification(message);
                    }
                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                }





            }
        }else {
            Toast.makeText(getApplicationContext(),"else",Toast.LENGTH_LONG).show();

        }
    }

    private void startNotification(String message){
        RemoteViews notificationView = new RemoteViews(getPackageName(),
                R.layout.notification_xml);
         notificationView.setTextViewText(R.id.message,message);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.heart2)
                        .setContentTitle("Zerito")
                        .setContentText(message)
                        .setContent(notificationView)
                    ;
        Intent resultIntent = new Intent(this, GcmActivity.class);
        mBuilder.setAutoCancel(true);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(GcmActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(3, mBuilder.build());

    }

    }



