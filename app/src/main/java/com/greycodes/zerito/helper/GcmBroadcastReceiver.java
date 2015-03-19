package com.greycodes.zerito.helper;

/**
 * Created by ajmal on 18/2/15.
 */
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.greycodes.zerito.MainActivity;
import com.greycodes.zerito.R;


/**
 * Handling of GCM messages.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    static final String TAG = "GCMDemo";
    public static int NOTIFICATION_ID = 1;
    int type;
    private NotificationManager mNotificationManager;


    NotificationCompat.Builder builder;
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            sendNotification("Send error: " + intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            sendNotification("Deleted messages on server: " + intent.getExtras().toString());
        } else {
            if(intent.getExtras().getString("Type").equals("1")){
                type=1;
                sendNotification(intent.getExtras().getString("Message"));
                //register

            }else if (intent.getExtras().getInt("Type")==2){
                //pair
                //mob1 initiates the request
                //mob2 gets the push
                //mob1,mob2,pin    mob2=pin
                //Mob_no,
                sendNotification(intent.getExtras().getString("Message"));
                type=2;
                Toast.makeText(ctx,"mobile "+intent.getExtras().getString("Mob_no"),Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"name "+intent.getExtras().getString("Name"),Toast.LENGTH_LONG).show();
               // Toast.makeText(ctx,intent.getExtras().getString("Mob_no"),Toast.LENGTH_LONG).show();


//mob1,mob2
            }
            else if (intent.getExtras().getString("Type").equals("3")){
                //wallpaper change
                //url mob1,mob2,img_id
                //after mob2=img_id
                String url ="";
                Intent service = new Intent(ctx,SetWallpaperService.class);
                service.putExtra("url",url);
                ctx.startService(service);
                type=3;
                Toast.makeText(ctx,intent.getExtras().getString("img_link"),Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,intent.getExtras().getString("Mob_no"),Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,intent.getExtras().getString("Name"),Toast.LENGTH_LONG).show();

            }else if (intent.getExtras().getString("Type").equals("4")){
                //geneeral notification
                type=4;
                sendNotification(intent.getExtras().getString("Message"));
            }

            Toast.makeText(ctx,"push ..",Toast.LENGTH_LONG).show();
        }
        setResultCode(Activity.RESULT_OK);
    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, MainActivity.class), 0);
        switch (type){
            case 4:
                //general noti
                contentIntent = PendingIntent.getActivity(ctx, 0,
                        new Intent(ctx, MainActivity.class), 0);
                break;
            case 1:
                //register
                contentIntent = PendingIntent.getActivity(ctx, 0,
                        new Intent(ctx, MainActivity.class), 0);
                break;
            case 2:
                //pair
                //mob1 initiates the request
                //mob2 gets the push
                //mob1,mob2,pin    mob2=pin
                //Mob_no,
                contentIntent = PendingIntent.getActivity(ctx, 0,
                        new Intent(ctx, MainActivity.class), 0);
                break;
            case 3:
                //wallpaper change
                //url mob1,mob2,img_id
                //after mob2=img_id
                contentIntent = PendingIntent.getActivity(ctx, 0,
                        new Intent(ctx, MainActivity.class), 0);
                break;
        }

        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Zerito")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setDefaults(defaults);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID++;
    }
}
