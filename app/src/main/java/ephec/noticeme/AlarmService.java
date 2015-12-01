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

    public void launchNotification(Alarm memo){
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(memo.getTitle())
                        .setContentText(memo.getDescription())
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder tsb = TaskStackBuilder.create(this);
        tsb.addParentStack(MemoOverviewActivity.class);
        tsb.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = tsb.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(memo.getId(), builder.build());

    }
}
