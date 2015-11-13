package ephec.noticeme;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmService extends IntentService {
    private static int mNotificationId = 0;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null){
            DBHelper db = new DBHelper(this);
            db.getReadableDatabase();
            Alarm memo = db.getAlarm(extras.getString("memoTitle"));
            db.close();
            launchNotification(memo);
        }
        launchNotification("Titre du memo", "Description du memo");
    }

    //TODO REDIRECT WHEN CLICK ON NOTIFICATION
    //TODO BUTTON SNOOZE AND DISSMISS
    public void launchNotification(String title,String description){
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);

        // Sets up the Snooze and Dismiss action buttons that will appear in the
        // expanded view of the notification.
        Intent dismissIntent = new Intent(this, MemoOverviewActivity.class);
        //dismissIntent.setAction("ACTION_DISMISS");
        dismissIntent.putExtra("memoTitle", title);
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        Intent snoozeIntent = new Intent(this, MemoOverviewActivity.class);
        //snoozeIntent.setAction("ACTION_SNOOZE");
        snoozeIntent.putExtra("memoTitle", title);
        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);

        // Constructs the Builder object.
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Je suis la description ! Il faut plein de texte alors je rajoute lpein plein de mot qui soule "))
                        .addAction (R.drawable.ic_action_cancel,
                                "dismiss", piDismiss)
                        .addAction (R.drawable.ic_action_plus,
                                "Snooze", piSnooze);

        /*
         * Clicking the notification itself displays ResultActivity, which provides
         * UI for snoozing or dismissing the notification.
         * This is available through either the normal view or big view.
         */
        Intent resultIntent = new Intent(this, MemoOverviewActivity.class);
        resultIntent.putExtra("memoTitle", title);
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        0
                );

        builder.setContentIntent(resultPendingIntent);
        mNotificationId++;
        mNotificationManager.notify(mNotificationId, builder.build());

    }
    public void launchNotification(Alarm memo){
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);

        // Sets up the Snooze and Dismiss action buttons that will appear in the
        // expanded view of the notification.
        Intent dismissIntent = new Intent(this, MemoOverviewActivity.class);
        //dismissIntent.setAction("ACTION_DISMISS");
        dismissIntent.putExtra("memoTitle", memo.getTitle());
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        Intent snoozeIntent = new Intent(this, MemoOverviewActivity.class);
        //snoozeIntent.setAction("ACTION_SNOOZE");
        snoozeIntent.putExtra("memoTitle", memo.getTitle());
        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);

        // Constructs the Builder object.
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(memo.getTitle())
                        .setContentText("Timer has expired !")
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(memo.getDescription()))
                        .addAction (R.drawable.ic_action_cancel,
                                "dismiss", piDismiss)
                        .addAction (R.drawable.ic_action_plus,
                                "Snooze", piSnooze);

        Intent resultIntent = new Intent(this, MemoOverviewActivity.class);
        resultIntent.putExtra("memoTitle", memo.getTitle());

        TaskStackBuilder tsb = TaskStackBuilder.create(this);
        tsb.addParentStack(MemoOverviewActivity.class);
        tsb.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = tsb.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent resultPendingIntent =
        //        PendingIntent.getActivity(this, 0, resultIntent, 0);

        builder.setContentIntent(resultPendingIntent);
        mNotificationId++;
        mNotificationManager.notify(mNotificationId, builder.build());

    }
}
