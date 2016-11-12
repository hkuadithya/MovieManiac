//package com.adithyaupadhya.moviemaniac.notification;
//
//import android.app.IntentService;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.content.Intent;
//import android.os.Build;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//
//import com.adithyaupadhya.moviemaniac.R;
//
//
///**
// * An {@link IntentService} subclass for handling asynchronous task requests in
// * a service on a separate handler thread.
// * <p>
// * TODO: Customize class - update intent actions and extra parameters.
// */
//public class NotificationIntentService extends IntentService {
//
//    public NotificationIntentService() {
//        super(NotificationIntentService.class.getName());
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//        Log.d("MMVOLLEY", "Inside notification service");
//
//        String title = "HELLO WORLD";
//        String content = "Hello world, hello world, hello world, hello world, hello world, hello world, hello world";
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setContentTitle(content)
//                .setContentText(title)
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("I was going through the Notifications design pattern, and didn't find anything that talks about notification icon background.")
//                        .setBigContentTitle("I was going through the Notifications design pattern, and didn't find anything that talks about notification icon background.")
//                        .setSummaryText("I was going through the Notifications design pattern, and didn't find anything that talks about notification icon background.")
//                );
//
//        getNotificationIcons(builder);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(100, builder.build());
//
//    }
//
//
//
//
//    private void getNotificationIcons(NotificationCompat.Builder builder) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder.setSmallIcon(R.drawable.vector_notification_icon);
//            builder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        } else {
//            builder.setSmallIcon(R.mipmap.ic_launcher);
//        }
//
//    }
//}
