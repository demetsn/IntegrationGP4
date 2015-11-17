package ephec.noticeme;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        /*Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();
        */
        //this will send a notification message
        //ComponentName comp = new ComponentName(context.getPackageName(),
        //        AlarmService.class.getName());
        Bundle extras = intent.getExtras();
        Intent service = new Intent(context,AlarmService.class);
        service.putExtra("memoTitle", extras.getString("memotitle"));
        startWakefulService(context, service);
        //startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);


    }
}
