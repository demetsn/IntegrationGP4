package ephec.noticeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GeoReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        Intent service = new Intent(context,AlarmService.class);
        service.putExtra("memoTitle", extras.getString("memoTitle"));
        startWakefulService(context, service);
        //startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
