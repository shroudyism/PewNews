package com.example.shroudyism.pewnews;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.example.shroudyism.wallpaperfinder.R;

import java.util.concurrent.atomic.AtomicInteger;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.app.PendingIntent.getActivity;
class NotificationID {
    private static final  AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}
public class NotificationUtils {



    private static final Integer ID = NotificationID.getID();

    public static PendingIntent contentIntent(Context context) {

        Intent startMainActivity = new Intent(context, MainActivity.class);

        return getActivity(context, ID, startMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static Bitmap largeIcon(Context context) {

        Resources res = Resources.getSystem();

        Bitmap icon = BitmapFactory.decodeResource(res, android.support.compat.R.drawable.notification_icon_background);


        return icon;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void remindUser(Context context) {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(

                    ID.toString(),
                    context.getString(R.string.main_notification_channel_name),
                    IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(mChannel);
        }


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, ID.toString())
                        .setColor(ContextCompat.getColor(context, R.color.cardview_dark_background))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("My Notification Title")
                        .setContentText("My Notification Text")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Notification Button Tapped"))
                        .setContentIntent(contentIntent(context))
                        .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(ID, notificationBuilder.build());
    }


}