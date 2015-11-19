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
            System.out.println(extras.getString("memoTitle"));
            db.close();
            launchNotification(memo);
        }
    }

    //TODO REDIRECT WHEN CLICK ON NOTIFICATION
    //TODO BUTTON SNOOZE

    public void launchNotification(Alarm memo){
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);

        // Sets up the Snooze and Dismiss action buttons that will appear in the
        // expanded view of the notification.

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
        mNotificationManager.notify(memo.getId(), builder.build());

    }
}
